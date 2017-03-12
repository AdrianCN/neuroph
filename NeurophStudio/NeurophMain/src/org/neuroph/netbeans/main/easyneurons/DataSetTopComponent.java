package org.neuroph.netbeans.main.easyneurons;

import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import org.jdesktop.application.Action;
import org.netbeans.spi.actions.AbstractSavable;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.netbeans.explorer.ExplorerDataSetNode;
import org.neuroph.netbeans.explorer.ExplorerTopComponent;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Confirmation;
import org.openide.explorer.ExplorerManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.SaveAsCapable;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays training set.
 */
public final class DataSetTopComponent extends TopComponent implements LookupListener, ExplorerManager.Provider {

    /**
     * path to the icon used by the component and its open action
     */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "DataSetTopComponent";
    private InstanceContent content = new InstanceContent();
    private AbstractLookup aLookup = new AbstractLookup(content);
    private FileObject fileObject;
    
    private DataSet dataSet;
    private DataSetTableModel tableModel;
    private String trainingSetType;
    private int inputs, outputs;
    private String trainingSetLabel;
    
    private final ExplorerManager explorerManager = new ExplorerManager();
    
    public DataSetTopComponent() {
        tableModel = new DataSetTableModel();
        initComponents();
        setName(NbBundle.getMessage(DataSetTopComponent.class, "CTL_TrainingSetEditFrameTopComponent"));
        setToolTipText(NbBundle.getMessage(DataSetTopComponent.class, "HINT_TrainingSetEditFrameTopComponent"));
//        setIcon(ImageUtilities.loadImage(ICON_PATH, true));
        putClientProperty(TopComponent.PROP_DRAGGING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.TRUE);
        //content = new InstanceContent();
    }
    
    public DataSetTopComponent(DataObject dataSetDataObject) {
        this.dataSet = dataSetDataObject.getNodeDelegate().getLookup().lookup(DataSet.class);
        this.fileObject = dataSetDataObject.getPrimaryFile();       

        tableModel = new DataSetTableModel();
        initComponents();
        setName(NbBundle.getMessage(DataSetTopComponent.class, "CTL_TrainingSetEditFrameTopComponent"));
        setToolTipText(NbBundle.getMessage(DataSetTopComponent.class, "HINT_TrainingSetEditFrameTopComponent"));
//        setIcon(ImageUtilities.loadImage(ICON_PATH, true));
        putClientProperty(TopComponent.PROP_DRAGGING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.TRUE);
        this.setTrainingSetEditFrameVariables(dataSet);
        
        explorerManager.setRootContext(new ExplorerDataSetNode(dataSet));
        
        content.add(dataSet); // dodati i ovo u lookup....
        content.add(new Save(this, content)); //TODO enable this on notifyModified
        content.add(new SaveAs(this));
    }
    
    @Override
    public Lookup getLookup() {
        return new ProxyLookup(
                new Lookup[]{
                    super.getLookup(),
                    aLookup
                });
    }
    
    @Override
    public void componentOpened() {
        if (this.dataSet != null) {
            setName(dataSet.getLabel());
        } else {
            setName("Training set not loaded");
        }
//        if (content == null) {
//            content = new InstanceContent();
//        }
        WindowManager.getDefault().findTopComponent("ExplorerTopComponent").open();
    }
    
    @Override
    public void componentClosed() {
        //  ViewManager.getInstance().onTrainingSetClose(dataSet);
        
        TopComponent exTC = WindowManager.getDefault().findTopComponent("ExplorerTopComponent");
        if (exTC != null) {
            ExplorerTopComponent explorerTC = (ExplorerTopComponent) exTC;
            if (explorerTC.isValidDSetInRoot(dataSet)) {
                explorerTC.emptyTree();
            }
        }
    }
    
    @Override
    protected void componentActivated() {
        super.componentActivated();
        // update table model here
        setTrainingSet(this.dataSet); // needs to be refreshe d if it is normalised for example...
        
        TopComponent tc = WindowManager.getDefault().findTopComponent("ExplorerTopComponent");
        if (tc != null) {
            ExplorerTopComponent explorerTC = (ExplorerTopComponent) tc;
            explorerTC.initializeOrSelectDSetRoot(dataSet);
        }
    }
     @Override
    public boolean canClose() {
        Save saveObj = getLookup().lookup(Save.class);
        if (saveObj != null) {
            Confirmation msg = new NotifyDescriptor.Confirmation(
                    "Do you want to save \"" + this.getName() + "\"?",
                    NotifyDescriptor.YES_NO_OPTION,
                    NotifyDescriptor.QUESTION_MESSAGE);
            Object result = DialogDisplayer.getDefault().notify(msg);
            if (NotifyDescriptor.YES_OPTION.equals(result)) {
                saveTopComponent();

                // disable "Save" option
                content.remove(saveObj);
                saveObj.unregisterPublic();
            } else if (NotifyDescriptor.NO_OPTION.equals(result)) {
                // disable "Save" option
                content.remove(saveObj);
                saveObj.unregisterPublic();
            } else {
                // do not close, as the dialog has been closed
                return false;
            }
        }
        return super.canClose();
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tablePopupMenu = new javax.swing.JPopupMenu();
        addRowMenuItem = new javax.swing.JMenuItem();
        delRowMenuItem = new javax.swing.JMenuItem();
        buttonPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        addRowButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        tableScrollPane = new javax.swing.JScrollPane();
        dataSetTable = new org.netbeans.swing.etable.ETable();

        org.openide.awt.Mnemonics.setLocalizedText(addRowMenuItem, org.openide.util.NbBundle.getMessage(DataSetTopComponent.class, "DataSetTopComponent.addRowMenuItem.text")); // NOI18N
        addRowMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRowMenuItemActionPerformed(evt);
            }
        });
        tablePopupMenu.add(addRowMenuItem);

        org.openide.awt.Mnemonics.setLocalizedText(delRowMenuItem, org.openide.util.NbBundle.getMessage(DataSetTopComponent.class, "DataSetTopComponent.delRowMenuItem.text")); // NOI18N
        delRowMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delRowMenuItemActionPerformed(evt);
            }
        });
        tablePopupMenu.add(delRowMenuItem);

        setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(okButton, org.openide.util.NbBundle.getMessage(DataSetTopComponent.class, "DataSetTopComponent.okButton.text")); // NOI18N
        okButton.setPreferredSize(new java.awt.Dimension(100, 29));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(okButton);

        org.openide.awt.Mnemonics.setLocalizedText(addRowButton, org.openide.util.NbBundle.getMessage(DataSetTopComponent.class, "DataSetTopComponent.addRowButton.text")); // NOI18N
        addRowButton.setPreferredSize(new java.awt.Dimension(100, 29));
        addRowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRowButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(addRowButton);

        org.openide.awt.Mnemonics.setLocalizedText(cancelButton, org.openide.util.NbBundle.getMessage(DataSetTopComponent.class, "DataSetTopComponent.cancelButton.text")); // NOI18N
        cancelButton.setPreferredSize(new java.awt.Dimension(100, 29));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(cancelButton);

        add(buttonPanel, java.awt.BorderLayout.SOUTH);

        dataSetTable.setModel(tableModel);
        dataSetTable.setComponentPopupMenu(tablePopupMenu);
        tableScrollPane.setViewportView(dataSetTable);

        add(tableScrollPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.close();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        if (dataSetTable.isEditing()) {
            dataSetTable.getCellEditor().stopCellEditing();
        }
           
        ArrayList<ArrayList> dataVector = this.tableModel.getDataVector();
        Iterator<ArrayList> iterator = dataVector.iterator();
        this.dataSet.clear();
        
        if (this.trainingSetType.equals("Unsupervised")) {
            while (iterator.hasNext()) {
                ArrayList rowVector = iterator.next();
                ArrayList<Double> doubleRowVector = new ArrayList<Double>();
                try {
                    for (int i = 0; i < this.inputs; i++) {
                        double doubleVal = Double.parseDouble(rowVector.get(i).toString());
                        doubleRowVector.add(new Double(doubleVal));
                    }
                } catch (Exception ex) {
                    continue;
                }
                
                DataSetRow trainingElement = new DataSetRow(doubleRowVector);
                this.dataSet.addRow(trainingElement);
            }
        } else if (this.trainingSetType.equals("Supervised")) {
            while (iterator.hasNext()) {
                ArrayList rowVector = iterator.next();
                ArrayList<Double> inputVector = new ArrayList<Double>();
                ArrayList<Double> outputVector = new ArrayList<Double>();
                
                try {
                    for (int i = 0; i < this.inputs; i++) {
                        double doubleVal = Double.parseDouble(rowVector.get(i).toString());
                        inputVector.add(new Double(doubleVal));
                    }
                    
                    for (int i = 0; i < this.outputs; i++) {
                        double doubleVal = Double.parseDouble(rowVector.get(
                                this.inputs + i).toString());
                        outputVector.add(new Double(doubleVal));
                    }
                } catch (Exception ex) {
                    continue;
                }
                
                DataSetRow trainingElement = new DataSetRow(
                        inputVector, outputVector);
                this.dataSet.addRow(trainingElement);
                
            }
        }

        // ovde cak i ne treba pisati u fajl vec se to radi na file > save
//          for (CreateTrainigSetFileServiceInterface fileservices : ServiceLoader.load(CreateTrainigSetFileServiceInterface.class)) {
//                fileservices.serialise(this.dataSet);
//            }
        // ovde treba kreirati dogadjaj TreningSetCreatedEvent i fireEvent
        // a neural network frame treba da slusa te dogadjaje. Kako dodati slusaoca???
        // potencilajni problem j esto se ti prozori otvaraju i zatvaraju
        // mozda najbolje to resiti preko lookup-a
//        ProjectManager.getInstance().updateTrainingSets(this.dataSet);
        //this.dispose();
        this.close();
    }//GEN-LAST:event_okButtonActionPerformed

    private void addRowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRowButtonActionPerformed
        ((DataSetTableModel) dataSetTable.getModel()).addEmptyRow();
    }//GEN-LAST:event_addRowButtonActionPerformed

    private void addRowMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRowMenuItemActionPerformed
        ((DataSetTableModel) dataSetTable.getModel()).addEmptyRow();
}//GEN-LAST:event_addRowMenuItemActionPerformed

    private void delRowMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delRowMenuItemActionPerformed
        ((DataSetTableModel) dataSetTable.getModel())
                .removeRow(dataSetTable.getSelectedRow());
}//GEN-LAST:event_delRowMenuItemActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addRowButton;
    private javax.swing.JMenuItem addRowMenuItem;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private org.netbeans.swing.etable.ETable dataSetTable;
    private javax.swing.JMenuItem delRowMenuItem;
    private javax.swing.JButton okButton;
    private javax.swing.JPopupMenu tablePopupMenu;
    private javax.swing.JScrollPane tableScrollPane;
    // End of variables declaration//GEN-END:variables

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }
   
    private void readPropertiesImpl(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
    
    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }
    
    public void setTrainingSetEditFrameVariables(DataSet trainingSet, String type, int inputs, int outputs) {
        this.trainingSetType = type;
        this.dataSet = trainingSet;
        this.inputs = inputs;
        this.outputs = outputs;
        this.tableModel = new DataSetTableModel(inputs, outputs);

        //tableModel.addTableModelListener(new TrainingSetEditFrame.InteractiveTableModelListener());
        tableModel.addTableModelListener(new DataSetTopComponent.InteractiveTableModelListener());
        
        initComponents();
        
        if (!tableModel.hasEmptyRow()) {
            tableModel.addEmptyRow();
        }
        dataSetTable.setSurrendersFocusOnKeystroke(true);
        
        TableColumn hidden = dataSetTable.getColumnModel().getColumn(tableModel.HIDDEN_INDEX);
        hidden.setMinWidth(2);
        hidden.setPreferredWidth(2);
        hidden.setMaxWidth(2);
        hidden.setCellRenderer(new InteractiveRenderer(tableModel.HIDDEN_INDEX));
        
        this.trainingSetLabel = trainingSet.getLabel();
        this.dataSetTable.getTableHeader().setReorderingAllowed(false);
    }
    
    public void setTrainingSetEditFrameVariables(DataSet trainingSet) {
        this.dataSet = trainingSet;
        this.tableModel = new DataSetTableModel(trainingSet);
        //associateLookup(Lookups.singleton(dataSet));

        //tableModel.addTableModelListener(new TrainingSetEditFrame.InteractiveTableModelListener());
        tableModel.addTableModelListener(new DataSetTopComponent.InteractiveTableModelListener());
        
        initComponents();
        
        if (!tableModel.hasEmptyRow()) {
            tableModel.addEmptyRow();
        }
        dataSetTable.setSurrendersFocusOnKeystroke(true);
        
        TableColumn hidden = dataSetTable.getColumnModel().getColumn(tableModel.HIDDEN_INDEX);
        hidden.setMinWidth(2);
        hidden.setPreferredWidth(2);
        hidden.setMaxWidth(2);
        hidden.setCellRenderer(new InteractiveRenderer(tableModel.HIDDEN_INDEX));
        
        this.trainingSetLabel = trainingSet.getLabel();

//        if (dataSet.getRows().size() > 0) {
//            DataSetRow trainingElement = (DataSetRow) dataSet.getRows().get(0);
        if (trainingSet.isSupervised()) {
            this.trainingSetType = "Supervised";
            this.inputs = trainingSet.getInputSize();
            //this.inputs = trainingElement.getInput().size();
            //this.outputs = ((SupervisedTrainingElement) trainingElement)
            //		.getDesiredOutput().length;
            this.outputs = trainingSet.getOutputSize();
        } else {
            this.trainingSetType = "Unsupervised";
            this.outputs = 0;
            //this.inputs = trainingElement.getInput().length;
            this.inputs = trainingSet.getInputSize();
            //          }
        }
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
//    @Action
//    public void cancel() {
//        //this.dispose();
//        this.close();
//    }
//    @Action
//    public void addtableRow() {
//        ((DataSetTableModel) trainingSetTable.getModel()).addEmptyRow();
//    }
    @Action
    public void deleteTableRow() {
        //int selected_row = dataSetTable.getSelectedRow();
        ((DataSetTableModel) dataSetTable.getModel()).removeRow(dataSetTable.getSelectedRow());
        
    }
//
//    @Action
//    public void save() {
//        if (trainingSetTable.isEditing()) {
//            trainingSetTable.getCellEditor().stopCellEditing();
//        }
//
//        if (this.traningSetLabelTextField.getText().trim().isEmpty()) {
//            javax.swing.JOptionPane.showMessageDialog(this, "Please enter the training set name!");
//            return;
//        }
//
//        this.dataSet.setLabel(this.traningSetLabelTextField.getText().trim());
//        Vector<Vector> dataVector = this.tableModel.getDataVector();
//        Iterator<Vector> iterator = dataVector.iterator();
//        this.dataSet.clear();
//
//        if (this.trainingSetType.equals("Unsupervised")) {
//            while (iterator.hasNext()) {
//                Vector rowVector = iterator.next();
//                Vector<Double> doubleRowVector = new Vector<Double>();
//                try {
//                    for (int i = 0; i < this.inputs; i++) {
//                        double doubleVal = Double.parseDouble(rowVector.elementAt(i).toString());
//                        doubleRowVector.add(new Double(doubleVal));
//                    }
//                } catch (Exception ex) {
//                    continue;
//                }
//
//                TrainingElement trainingElement = new TrainingElement(
//                        doubleRowVector);
//                this.dataSet.addElement(trainingElement);
//            }
//        } else if (this.trainingSetType.equals("Supervised")) {
//            while (iterator.hasNext()) {
//                Vector rowVector = iterator.next();
//                Vector<Double> inputVector = new Vector<Double>();
//                Vector<Double> outputVector = new Vector<Double>();
//
//                try {
//                    for (int i = 0; i < this.inputs; i++) {
//                        double doubleVal = Double.parseDouble(rowVector.elementAt(i).toString());
//                        inputVector.add(new Double(doubleVal));
//                    }
//
//                    for (int i = 0; i < this.outputs; i++) {
//                        double doubleVal = Double.parseDouble(rowVector.elementAt(
//                                this.inputs + i).toString());
//                        outputVector.add(new Double(doubleVal));
//                    }
//                } catch (Exception ex) {
//                    continue;
//                }
//
//                SupervisedTrainingElement trainingElement = new SupervisedTrainingElement(
//                        inputVector, outputVector);
//                this.dataSet.addElement(trainingElement);
//            }
//        }
//
//        ProjectManager.getInstance().updateTrainingSets(this.dataSet);
//        //this.dispose();
//        this.close();
//
//    }

    public void highlightLastRow(int row) {
        int lastrow = tableModel.getRowCount();
        if (row == lastrow - 1) {
            dataSetTable.setRowSelectionInterval(lastrow - 1, lastrow - 1);
        } else {
            dataSetTable.setRowSelectionInterval(row + 1, row + 1);
        }
        
        dataSetTable.setColumnSelectionInterval(0, 0);
    }
    
    @Override
    public void resultChanged(LookupEvent ev) {
    }
    
    class InteractiveRenderer extends DefaultTableCellRenderer {
        
        protected int interactiveColumn;
        
        public InteractiveRenderer(int interactiveColumn) {
            this.interactiveColumn = interactiveColumn;
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column == interactiveColumn && hasFocus) {
//                if ((TrainingSetEditFrame.this.tableModel.getRowCount() - 1) == row
//                        && !TrainingSetEditFrame.this.tableModel.hasEmptyRow()) {
//                    TrainingSetEditFrame.this.tableModel.addEmptyRow();
//                }
                if ((DataSetTopComponent.this.tableModel.getRowCount() - 1) == row
                        && !DataSetTopComponent.this.tableModel.hasEmptyRow()) {
                    DataSetTopComponent.this.tableModel.addEmptyRow();
                }
                
                highlightLastRow(row);
            }
            
            return c;
        }
    }
    
    public class InteractiveTableModelListener implements TableModelListener {
        
        public void tableChanged(TableModelEvent evt) {
            if (evt.getType() == TableModelEvent.UPDATE) {
                int column = evt.getColumn();
                int row = evt.getFirstRow();
                // System.out.println("row: " + row + " column: " + column);
                dataSetTable.setColumnSelectionInterval(column + 1, column + 1);
                dataSetTable.setRowSelectionInterval(row, row);
            }
        }
    }

//    @Action
//    public void showLoadTrainingSetDialog() {
//        TrainingDataFileDialog dialog = new TrainingDataFileDialog(inputs, outputs, this);
//        dialog.setVisible(true);
////        EasyNeuronsViewController.getInstance().show(dialog);
//    }
    public void setTrainingSet(DataSet trainingSet) {
        this.dataSet = trainingSet;
        
        this.tableModel = new DataSetTableModel(this.dataSet);
        this.dataSetTable.setModel(this.tableModel);
        dataSetTable.setSurrendersFocusOnKeystroke(true);
        
        TableColumn hidden = dataSetTable.getColumnModel().getColumn(tableModel.HIDDEN_INDEX);
        hidden.setMinWidth(2);
        hidden.setPreferredWidth(2);
        hidden.setMaxWidth(2);
        hidden.setCellRenderer(new InteractiveRenderer(tableModel.HIDDEN_INDEX));
        
        this.tableModel.fireTableDataChanged();
        
    }
    
    private void setTableModel() {
        if (dataSet.size() > 0) {
            DataSetRow trainingElement = (DataSetRow) dataSet.getRowAt(0);
            if (trainingElement.isSupervised()) {
                this.trainingSetType = "Supervised";
                this.inputs = trainingElement.getInput().length;
                //this.inputs = trainingElement.getInput().size();
                //this.outputs = ((SupervisedTrainingElement) trainingElement)
                //		.getDesiredOutput().length;
                // this.outputs = ((SupervisedTrainingElement) trainingElement).getDesiredOutput().size();
                this.outputs = trainingElement.getDesiredOutput().length;
            } else {
                this.trainingSetType = "Unsupervised";
                this.outputs = 0;
                this.inputs = trainingElement.getInput().length;
                //this.inputs = trainingElement.getInput().size();
            }
        }
    }
    
    private void saveTopComponentToPath(String path) {
        dataSet.save(path);
    }
    
    private void saveTopComponent() {
        String filePath = fileObject.getPath();
        dataSet.save(filePath);
    }

    /**
     * Enables SaveAs functionality.
     */
    private class SaveAs implements SaveAsCapable {

        DataSetTopComponent dataSetTopComponent;

        public SaveAs(DataSetTopComponent dataSetTopComponent) {
            this.dataSetTopComponent = dataSetTopComponent;
        }

        @Override
        public void saveAs(FileObject folder, String name) throws IOException {
            String path = folder.getPath() + "/" + name;
            dataSetTopComponent.saveTopComponentToPath(path);
        }
    }

    /**
     * Enables Save functionality.
     */    
    public class Save extends AbstractSavable {
        
        private final DataSetTopComponent dataSetTopComponent;
        private final InstanceContent ic;
        
        public Save(DataSetTopComponent topComponent, InstanceContent instanceContent) {
            this.dataSetTopComponent = topComponent;
            this.ic = instanceContent;
            register();
        }
        
        @Override
        protected String findDisplayName() {
            return "Data set " + dataSetTopComponent.getName(); // get display name somehow
        }

        /**
         * Exposes unregister() method so that this Save object can be removed
         * from SavableRegistry if the file has been saved in some other way.
         * A user can save the file on closing of the UMLTopComponent, if it has
         * not been saved.
         */
        public void unregisterPublic() {
            unregister();
        }
        
        @Override
        protected void handleSave() throws IOException {
//            Confirmation msg = new NotifyDescriptor.Confirmation(
//                    "Do you want to save \"" + umlTopComponent.getName() + "\"?",
//                    NotifyDescriptor.OK_CANCEL_OPTION,
//                    NotifyDescriptor.QUESTION_MESSAGE);
//            Object result = DialogDisplayer.getDefault().notify(msg);
//            //When user clicks "Yes", indicating they really want to save,
//            //we need to disable the Save button and Save menu item,
//            //so that it will only be usable when the next change is made
//            // save 'obj' somehow
//            if (NotifyDescriptor.OK_OPTION.equals(result)) {
            dataSetTopComponent.saveTopComponent();

//            ic.remove(this);  //TODO reenable this on notifyModified
//            } else {
//                throw new IOException();
//            }
        }
        
        @Override
        public boolean equals(Object other) {
            if (other instanceof Save) {
                return ((Save) other).dataSetTopComponent.equals(dataSetTopComponent);
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return dataSetTopComponent.hashCode();
        }
    }
}
