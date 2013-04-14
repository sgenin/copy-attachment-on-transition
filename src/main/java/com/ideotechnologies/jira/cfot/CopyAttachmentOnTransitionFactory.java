package com.ideotechnologies.jira.cfot;

import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: s.genin
 * Date: 08/04/13
 * Time: 17:23
 * To change this template use File | Settings | File Templates.
 */


public class CopyAttachmentOnTransitionFactory extends AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory {

    final String ROOT_DIR="rootdir";
    final String DELETE_FILE="deletefile";
    private Logger log = LoggerFactory.getLogger(CopyAttachmentOnTransitionFactory.class);

    @Override
    protected void getVelocityParamsForInput(Map<String, Object> stringObjectMap) {
        stringObjectMap.put(ROOT_DIR,"/");
        stringObjectMap.put(DELETE_FILE,false);
    }

    @Override
    protected void getVelocityParamsForEdit(Map<String, Object> stringObjectMap, AbstractDescriptor descriptor) {
        stringObjectMap.put(ROOT_DIR,getRootDir(descriptor));
        stringObjectMap.put(DELETE_FILE,getDeleteFile(descriptor));
    }

    @Override
    protected void getVelocityParamsForView(Map<String, Object> stringObjectMap, AbstractDescriptor descriptor) {
        stringObjectMap.put(ROOT_DIR,getRootDir(descriptor));
      stringObjectMap.put(DELETE_FILE,getDeleteFile(descriptor));
    }

    @Override
    public Map<String, ?> getDescriptorParams(Map<String, Object> stringObjectMap) {
        Map<String, String> params = new HashMap<String, String>();

        try{
            String rootDir = extractSingleParam(stringObjectMap, ROOT_DIR);
            params.put(ROOT_DIR, rootDir);
        } catch(IllegalArgumentException e) {
            log.warn(e.getMessage(), e);
        }
        try{
        String deleteFile = extractSingleParam(stringObjectMap, DELETE_FILE);
            params.put(DELETE_FILE, deleteFile);
        } catch(IllegalArgumentException e) {
            params.put(DELETE_FILE, "no");
        }
        return params;
    }

   private String getRootDir(AbstractDescriptor descriptor) {
        if (!(descriptor instanceof FunctionDescriptor)) {
            throw new IllegalArgumentException("Descriptor must be a FunctionDescriptor");
        }
        FunctionDescriptor functionDescriptor = (FunctionDescriptor)descriptor;

        String rootDir=(String) functionDescriptor.getArgs().get(ROOT_DIR);
        return rootDir;
    }

    private String getDeleteFile(AbstractDescriptor descriptor) {
        if (!(descriptor instanceof FunctionDescriptor)) {
            throw new IllegalArgumentException("Descriptor must be a FunctionDescriptor");
        }
        FunctionDescriptor functionDescriptor = (FunctionDescriptor)descriptor;

        String deleteFile= (String)functionDescriptor.getArgs().get(DELETE_FILE);
        return deleteFile;
    }
}
