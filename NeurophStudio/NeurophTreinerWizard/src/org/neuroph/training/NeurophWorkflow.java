package org.neuroph.training;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author zoran
 * 
 *  preprocess data
 *  create several training, test and validation sets out of whole given data set (specify in percentage and how many of them,  and then select randomly elements)
 *      automatsko generisanje data setova, ili zadavanje
 *  create several neural networks with different settings/architectures/number of layers and neurons
 *  run training (with validation set, if it doesent stop continue)
 *  now run training with training set
 *  and at the end run the test set
 * 
 * PostProcess - Report; Sensitivity Analysys
 * 
 *  1. Pre-processing - normalization
    2. Chop the data - generate random training subsets
 *  3. Validation - do the test run will the algorithn diverge, too slow...?
    4. Training - do the actual training
 *  5. Test task
    6. Report - give brief summary about training
 *   - some steps might be ommited
 *   - need to create base classses for each type of step, and define inputs and outputs so tasks can communicate (exchange their outputs and inputs)
 * 
 * ----------------------------------------
 * 
 * api u osnovi da bude jednostavan ali da moze da se prosiri za kompleksne stvari
 * 
 * this could be !!!!! Neuroph training container!!!!
 * variables can be injected into tasks
 * workflow!!!
 * 
 * see Injector http://code.google.com/p/tinydi/source/browse/trunk/TinyDI/src/main/java/com/googlecode/tinydi/Injector.java
 * http://stackoverflow.com/questions/9392182/creating-a-simple-dependency-injection-container-in-java-dynamically-creating-o
 * 
 */
public class NeurophWorkflow { // rename to NeurophWorkflow - ili NeurophPipeline : provides predefined customizable workflows
    ArrayList<Task> tasks;
  
    
    int currentTaskIndex;
    Task currentTask;
    Object taskInputOutput;
    StringBuilder processLog;
    
    HashMap<String, Variable> variables;
    
    private List<ProcessEventListener> listeners = new ArrayList<>();

    public NeurophWorkflow() {
        tasks = new ArrayList<>();
        variables = new HashMap<>();
    }
    
    public NeurophWorkflow addTask(Task task) {
        task.setParentProcess(this);
        tasks.add(task);
        return this;
    }
    
    public NeurophWorkflow addTask(int idx, Task task) {
        task.setParentProcess(this);
        tasks.add(idx, task);
        return this;
    }    

    public ArrayList<Task> getTasks() {
        return tasks;
    }
    
    public Task getTaskAt(int index) {
        return tasks.get(index);
    }
    
    public void removeTaskAt(int index) {
        // set parent process to null
        tasks.remove(index);
    }   
    
    public void jumpToTask(int taskIdx){
        this.currentTaskIndex = taskIdx-1;
    }
    
    public void jumpToTask(String taskName){
        int jumpToIdx = getTaskIdxByName(taskName);        
        this.currentTaskIndex = jumpToIdx;
    }    
    
    public void execute() {
        processLog = new StringBuilder(); // use logger instead!
        currentTaskIndex = 0;
        while(currentTaskIndex < tasks.size()) {
            currentTask = tasks.get(currentTaskIndex);                       
                        
            logMessage("\n"+(currentTaskIndex+1) +". Running task "+currentTask.getName());
                   
            currentTask.execute();                           
            currentTaskIndex++;
        }        
    }
       
    public void setVariable(Variable<?> var) {
        variables.put(var.getName(), var);
        //processLog.append("Setting process var: "+name +"\n");
    }
    
    public Variable getVariable(String name) {
        if (!variables.containsKey(name))
            throw new RuntimeException("Process does not contain var "+name);
        return variables.get(name);
    }
    
    protected void logMessage(String msg) {
        processLog.append(msg).append(System.lineSeparator());
        fireProcessEvent(new ProcessEvent(this, msg));     
    }    
    
    public int getTaskIdx(Task task) {
        return tasks.indexOf(task);
    }

    public int getTaskIdxByName(String name) {
        for(int i=0; i<tasks.size(); i++) {
            if (tasks.get(i).name.equals(name)) {
                return i;
            }
        }        
        return -1;
    }
    
    
    public String getProcessLog() {
        return processLog.toString();
    }
    
    // This methods allows classes to register for ProcessEvents
    public synchronized void addListener(ProcessEventListener listener) {
        listeners.add(listener);
    }

    // This methods allows classes to unregister for ProcessEvents
    public synchronized void removeListener(ProcessEventListener listener) {
        listeners.remove(listener);
    }

    // This method is used to fire ProcessEvents
    public synchronized void fireProcessEvent(ProcessEvent event) {
        for (ProcessEventListener listener: listeners) {
            listener.handleProcessEvent(event);
        }
    }   
          
}