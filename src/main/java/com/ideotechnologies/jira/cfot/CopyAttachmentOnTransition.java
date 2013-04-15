package com.ideotechnologies.jira.cfot;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.RemoveException;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.AttachmentUtils;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowManager;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.google.common.io.Files;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.loader.ActionDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

//import com.atlassian.jira.util.JiraUtils;


/**
 * Created with IntelliJ IDEA.
 * User: s.genin
 * Date: 08/04/13
 * Time: 17:25
 * To change this template use File | Settings | File Templates.
 */
public class CopyAttachmentOnTransition extends AbstractJiraFunctionProvider{

    private final IssueManager issueManager;
    private final WorkflowManager workflowManager;
    private final JiraAuthenticationContext authenticationContext;
    private final String DEFAULT_DIR="default";
    private Logger log = LoggerFactory.getLogger(CopyAttachmentOnTransitionFactory.class);

    public CopyAttachmentOnTransition (IssueManager issueManager, WorkflowManager workflowManager, JiraAuthenticationContext authenticationContext)
    {
        this.issueManager = ComponentAccessor.getIssueManager();
        this.workflowManager = ComponentAccessor.getWorkflowManager();
        this.authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
    }

    @Override
    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {

        AttachmentManager attachmentManager = ComponentAccessor.getAttachmentManager();
        Issue issue=this.getIssue(transientVars);
        List<Attachment> attachmentsList = attachmentManager.getAttachments(issue);
        Boolean success=true;

        String rootDir = (String) args.get("rootdir");
        String customFieldToSet = (String)args.get("selectedcustomfield");
        String shallDelete=(String)args.get("deletefile");
        for (Attachment attachment:attachmentsList) {

            File fileOrigin=AttachmentUtils.getAttachmentFile(issue, attachment);
            String subDir = getSubDirectory(issue,rootDir);
            if (subDir != null) {
                File fileDestination=new File(rootDir+System.getProperty("file.separator")+subDir+System.getProperty("file.separator")+attachment.getFilename());
                try {
                    Files.copy(fileOrigin, fileDestination);
                } catch (IOException e) {
                    success=false;
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }


                if ((shallDelete.compareTo("yes") == 0) && (success == true)){

                    try {
                        attachmentManager.deleteAttachment(attachment);
                    } catch (RemoveException e) {
                        success=false;
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
            else {
                success=false;
            }

        }

        CustomField customField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(customFieldToSet);
        String value=new String();
        if (success == true) {
            value=  (String) args.get("successvalue");
        }
        else {
            value=  (String) args.get("errorvalue");
        }

        customField.updateValue(null,issue,new ModifiedValue(issue.getCustomFieldValue(customField),value),new DefaultIssueChangeHolder());

    }


    private ActionDescriptor getTransition(Issue issue, Boolean success) {

        JiraWorkflow wf = ComponentAccessor.getWorkflowManager().getWorkflow(issue);
        Collection<ActionDescriptor> allActions = wf.getAllActions();

        for (ActionDescriptor ad : allActions) {
            Map <String,String> attributes = ad.getMetaAttributes();
            if (attributes.size() != 0) {
               if ((attributes.get("AutoTransitionIfOK").equals("true")) && (success == true)) {
                   return ad;
               }
               else if ((attributes.get("AutoTransitionIfKO")=="true") && (success == false)) {
                   return ad;
               }
            }
        }

        return null;
    }

    private String getSubDirectory(Issue issue, String rootDir) {
        Boolean success=true;
        String securityLevel = ComponentAccessor.getIssueSecurityLevelManager().getIssueSecurityName(issue.getSecurityLevelId());

        if (securityLevel == null) {
            // The file will be copied in the default subdirectory
            securityLevel=DEFAULT_DIR;
        }

        securityLevel=securityLevel.replace(' ','_');
        securityLevel=securityLevel.replace('\\','_');
        securityLevel=securityLevel.replace('/','_');

        // Check if the subdirectory exists.

        File subDir=new File(rootDir+System.getProperty("file.separator")+securityLevel);

        if (!subDir.exists()) {
           success = subDir.mkdir();
        }
        else {
            if (!subDir.isDirectory()) {
                success=false;
            }
        }

        if (success)
            return securityLevel;
        else
            return null;
    }

}
