package com.ideotechnologies.jira.cfot;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.customfields.impl.GenericTextCFType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    final String SELECTED_CUSTOM_FIELD="selectedcustomfield";
    final String SELECTED_CUSTOM_FIELD_NAME="selectedcustomfieldname";
    final String SUCCESS_VALUE="successvalue";
    final String ERROR_VALUE="errorvalue";
    final String CUSTOM_FIELDS="customfields";
    private Logger log = LoggerFactory.getLogger(CopyAttachmentOnTransitionFactory.class);

    @Override
    protected void getVelocityParamsForInput(Map<String, Object> stringObjectMap) {
        stringObjectMap.put(ROOT_DIR,"/");
        stringObjectMap.put(DELETE_FILE,"no");
        stringObjectMap.put(SELECTED_CUSTOM_FIELD,"");
        stringObjectMap.put(SUCCESS_VALUE,"");
        stringObjectMap.put(ERROR_VALUE,"");
        List<CustomField> customFields = ComponentAccessor.getCustomFieldManager().getCustomFieldObjects();
        List<CustomField> SelectableCustomFields=new ArrayList<CustomField>();
        for (CustomField cf : customFields) {
            if (cf.getCustomFieldType().getKey().equals("toto")) {
                SelectableCustomFields.add(cf);
            }
        }
        stringObjectMap.put(CUSTOM_FIELDS,SelectableCustomFields);    }

    @Override
    protected void getVelocityParamsForEdit(Map<String, Object> stringObjectMap, AbstractDescriptor descriptor) {
        getVelocityParamsForView(stringObjectMap, descriptor);
        List<CustomField> customFields = ComponentAccessor.getCustomFieldManager().getCustomFieldObjects();
        List<CustomField> SelectableCustomFields=new ArrayList<CustomField>();
        for (CustomField cf : customFields) {
            if (GenericTextCFType.class.isAssignableFrom(cf.getCustomFieldType().getClass()))  {
//            if (cf.getCustomFieldType().getKey().endsWith(CUSTOM_FIELD_TYPE_TEXTFIELD)) {
                SelectableCustomFields.add(cf);
            }
        }
        stringObjectMap.put(CUSTOM_FIELDS,SelectableCustomFields);
    }

    @Override
    protected void getVelocityParamsForView(Map<String, Object> stringObjectMap, AbstractDescriptor descriptor) {
     stringObjectMap.put(ROOT_DIR,getRootDir(descriptor));
     stringObjectMap.put(DELETE_FILE,getDeleteFile(descriptor));
     stringObjectMap.put(SELECTED_CUSTOM_FIELD,getSelectedCustomField(descriptor));
     stringObjectMap.put(SUCCESS_VALUE,getSuccessValue(descriptor));
     stringObjectMap.put(ERROR_VALUE,getErrorValue(descriptor));
     stringObjectMap.put(SELECTED_CUSTOM_FIELD_NAME,getSelectedCustomFieldName(descriptor));
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
        try{
            String selectedCustomField = extractSingleParam(stringObjectMap, SELECTED_CUSTOM_FIELD);
            params.put(SELECTED_CUSTOM_FIELD, selectedCustomField);
        } catch(IllegalArgumentException e) {
            log.warn(e.getMessage(), e);
        }
        try{
            String successValue = extractSingleParam(stringObjectMap, SUCCESS_VALUE);
            params.put(SUCCESS_VALUE, successValue);
        } catch(IllegalArgumentException e) {
            log.warn(e.getMessage(), e);
        }
        try{
            String errorValue = extractSingleParam(stringObjectMap, ERROR_VALUE);
            params.put(ERROR_VALUE, errorValue);
        } catch(IllegalArgumentException e) {
            log.warn(e.getMessage(), e);
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

    private String getSelectedCustomField(AbstractDescriptor descriptor) {
        if (!(descriptor instanceof FunctionDescriptor)) {
            throw new IllegalArgumentException("Descriptor must be a FunctionDescriptor");
        }
        FunctionDescriptor functionDescriptor = (FunctionDescriptor)descriptor;

        String selectedCustomField= (String)functionDescriptor.getArgs().get(SELECTED_CUSTOM_FIELD);
        return selectedCustomField;
    }

    private String getSelectedCustomFieldName(AbstractDescriptor descriptor) {
        if (!(descriptor instanceof FunctionDescriptor)) {
            throw new IllegalArgumentException("Descriptor must be a FunctionDescriptor");
        }
        FunctionDescriptor functionDescriptor = (FunctionDescriptor)descriptor;

        String selectedCustomField= (String)functionDescriptor.getArgs().get(SELECTED_CUSTOM_FIELD);

        CustomField customField=ComponentAccessor.getCustomFieldManager().getCustomFieldObject(selectedCustomField);

        return customField.getName();
    }

    private String getSuccessValue(AbstractDescriptor descriptor) {
        if (!(descriptor instanceof FunctionDescriptor)) {
            throw new IllegalArgumentException("Descriptor must be a FunctionDescriptor");
        }
        FunctionDescriptor functionDescriptor = (FunctionDescriptor)descriptor;

        String valueToSet= (String)functionDescriptor.getArgs().get(SUCCESS_VALUE);
        return valueToSet;
    }

    private String getErrorValue(AbstractDescriptor descriptor) {
        if (!(descriptor instanceof FunctionDescriptor)) {
            throw new IllegalArgumentException("Descriptor must be a FunctionDescriptor");
        }
        FunctionDescriptor functionDescriptor = (FunctionDescriptor)descriptor;

        String valueToSet= (String)functionDescriptor.getArgs().get(ERROR_VALUE);
        return valueToSet;
    }
}
