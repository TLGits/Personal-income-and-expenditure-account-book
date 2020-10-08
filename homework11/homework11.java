package homework11;

import java.io.File;
import java.io.FileReader;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;
import java.awt.*;
import javax.swing.*;

/**
 * 个人收支记账本--应用程序
 * 功能：1. 读取txt文件中记录的用户的收支情况并显示；
 *      2. 插入收支情况记录表，系统自动计算该用户的结余金额。
 * 配置&使用方法：将homework11压缩包解压缩，添加到项目src文件夹下，直接点击运行；
 *             点击“选择文本文件载入”，（默认路径为D盘）选择本文件夹下的file.txt，即可显示收支情况；
 *             点击"插入收支记录"，填写收入，支出（均为Double类型），系统自动更新计算结余金额。
 * 文件说明：homework11.java：项目代码；
 *         file.txt: 用户的收支情况txt记录文件；
 *         image.jpg: 用户的头像；
 * 采用技术：1. Swing下JFrame/JButton的窗体及组件 图形化用户界面；
 *         2. 图像读取并显示功能：ImageIcon函数；
 *         3. txt文件读取功能，自主选择文件；
 *         4. 自动计算结余金额，根据上一次的结余金额，本次的收入，支出，计算新的一轮结余金额；
 *         5. 定义个人情况为Users类，保护数据隐私，方便数据读取。
 */
public class homework11 extends JFrame {
    private JFrame frame = null;

    private File file = null;
    private FileReader fileReader = null;
    private JFileChooser fileChooser = null;

    private JLabel image = null;
    private JLabel title = null;
    private JButton loadin = null;

    private DefaultTableModel tableModel = null;
    private JTable table = null;

    private JButton btnInsert = null;

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new homework11();
        });
    }
    public homework11() {
        initialize();
    }

    private void initialize() {
        JPanel pnlTop = new JPanel();
        ImageIcon img = new ImageIcon("src\\homework11\\image.jpg");
        img.setImage(img.getImage().getScaledInstance(30,30,Image.SCALE_DEFAULT));
        image = new JLabel(img);
        title = new JLabel("个人收支管理", JLabel.CENTER);
        loadin = new JButton("选择文本文件载入");
        pnlTop.add(image);
        pnlTop.add(title);
        pnlTop.add(loadin);

        tableModel = new DefaultTableModel();
        table=new JTable(tableModel);

        JPanel pnlBottom = new JPanel();
        btnInsert = new JButton("插入收支记录");
        pnlBottom.add(btnInsert);

        frame = new JFrame("个人收支管理");
        frame.add(pnlTop, BorderLayout.NORTH);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.add(pnlBottom, BorderLayout.SOUTH);

        loadin.addActionListener(e -> doLoadin());
        btnInsert.addActionListener(e -> doInsert());

        frame.setSize(500, 500);
        frame.setLocation(300, 150);
        frame.setVisible(true);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * 载入收支记录文本文件
     */
    private void doLoadin() {
        fileChooser=new JFileChooser("D:\\");
        if(fileChooser.showOpenDialog(frame)==JFileChooser.APPROVE_OPTION) {
            file=fileChooser.getSelectedFile();
            try {
                fileReader=new FileReader(file);
                Vector<String> vector=new Vector<String>();
                vector.add("ID");vector.add("姓名");vector.add("收入");vector.add("支出");vector.add("结余");
                Vector<Vector<String>> dataVector=new Vector<Vector<String>>();
                Vector<String> dataVector2=new Vector<String>();
                String string="";
                while(fileReader.ready()) {
                    char ch=(char) fileReader.read();
                    if(ch==' ') {
                        dataVector2.add(string);
                        string="";
                    }
                    else if(ch=='\n') {
                        dataVector2.add(string);
                        string="";
                        dataVector.add(dataVector2);
                        dataVector2=new Vector<String>();
                    }
                    else {
                        string=string+ch;
                    }
                }
                tableModel.setDataVector(dataVector, vector);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 插入收支记录
     */
    private void doInsert(){
        double lastsurplus = Double.parseDouble(tableModel.getValueAt(tableModel.getRowCount()-1, tableModel.getColumnCount()-1).toString());

        ContactsDetailDialog inputDialog = new ContactsDetailDialog();
        inputDialog.uiClear();
        inputDialog.setVisible(true);
        System.out.println(inputDialog.isOkPressed());
        if (!inputDialog.isOkPressed())
            return;

        Users inputContact = inputDialog.ui2entity();
        System.out.println(inputContact);
        
        Vector<String> newdataVector=new Vector<String>();
        newdataVector.add(tableModel.getValueAt(tableModel.getRowCount()-1, 0).toString());
        newdataVector.add(tableModel.getValueAt(tableModel.getRowCount()-1, 1).toString());
        newdataVector.add(inputContact.getIncome().toString());
        newdataVector.add(inputContact.getExpense().toString());
        newdataVector.add(String.valueOf((lastsurplus+inputContact.getIncome()-inputContact.getExpense())));

        tableModel.addRow(newdataVector);
    }
}

/**
 * 插入界面
 */
class ContactsDetailDialog extends JDialog {
    boolean okPressed = false;

    private JLabel lblIncome = null;
    private JTextField txtIncome = null;
    private JLabel lblExpense = null;
    private JTextField txtExpense = null;

    private JPanel pnlInput = null;
    private JPanel pnlButtons = null;

    private JButton btnOk = null;
    private JButton btnCancel = null;

    public ContactsDetailDialog() {
        lblIncome = new JLabel("收入");
        txtIncome = new JTextField();
        lblExpense = new JLabel("支出");
        txtExpense = new JTextField();

        pnlInput = new JPanel();
        pnlInput.setLayout(new GridLayout(2, 2));

        pnlInput.add(lblIncome);
        pnlInput.add(txtIncome);
        pnlInput.add(lblExpense);
        pnlInput.add(txtExpense);

        pnlButtons = new JPanel();
        btnOk = new JButton("Ok");
        btnCancel = new JButton("Cancel");
        pnlButtons.setLayout(new FlowLayout(FlowLayout.CENTER));
        pnlButtons.add(btnOk);
        pnlButtons.add(btnCancel);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(pnlInput, BorderLayout.CENTER);
        getContentPane().add(pnlButtons, BorderLayout.SOUTH);

        setSize(300, 150);
        this.setModal(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnOk.addActionListener(e -> {
            okPressed = true;
            dispose();
        });
        btnCancel.addActionListener(e -> {
            okPressed = false;
            dispose();
        });
    }

    public boolean isOkPressed() {
        return okPressed;
    }

    public Users ui2entity() {
        Users c = new Users();
        c.setIncome(Double.parseDouble(txtIncome.getText()));
        c.setExpense(Double.parseDouble(txtExpense.getText()));
        return c;
    }

    public void uiClear() {
        txtIncome.setText("");
        txtExpense.setText("");
    }
}

/**
 * 个人收支情况类
 */
class Users {
    private int id;
    private String name = null;
    private Double income = null;
    private Double expense = null;
    private Double surplus = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    public Double getExpense() {
        return expense;
    }

    public void setExpense(Double expense) {
        this.expense = expense;
    }

    public Double getSurplus() {
        return surplus;
    }

    public void setSurplus(Double surplus) {
        this.surplus = surplus;
    }

}