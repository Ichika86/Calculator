/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author Filip
 */

import java.util.Stack;
import javax.swing.JFrame;



public class Calculator extends javax.swing.JFrame {
    
    
    private int positionX = 0;
    private int positionY = 0;
    /**
     * Creates new form NewJFrame
     */
    

    public Calculator() {
         initComponents();
         
          // Auto Answer 
          
        jtxtResult.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateLiveResult();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateLiveResult();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateLiveResult();
            }
        });
    }

    private void updateLiveResult() {
    String input = jtxtResult.getText().trim();


    if (input.isEmpty() || input.matches("\\d+")) {
        return; 
    }

    try {
        double result = evaluateInfix(input);
        jtxtLiveResult.setText((result == (long) result) 
            ? String.valueOf((long) result) 
            : String.valueOf(result));
    } catch (Exception e) {
        jtxtLiveResult.setText("");
    }
}

    private void evaluateExpression() {
    try {
        String infixExpression = jtxtResult.getText().trim();

        if (infixExpression.isEmpty() || !isValidExpression(infixExpression)) {
            jtxtResult.setText("Error: Invalid Input");
            clearExpressionDisplays();
            return;
        }

        infix.setText(infixExpression);

        String postfixExpression = infixToPostfix(infixExpression);
        String prefixExpression = infixToPrefix(infixExpression);

        double result = evaluateInfix(infixExpression);

        postfix.setText(postfixExpression);
        prefix.setText(prefixExpression);

        jtxtResult.setText((result == (long) result) ? String.valueOf((long) result) : String.valueOf(result));
        isResultDisplayed = true; 
    } catch (ArithmeticException e) {
        jtxtResult.setText("Error: Division by Zero");
        clearExpressionDisplays();
        isResultDisplayed = false;
    } catch (Exception e) {
        jtxtResult.setText("Error: Invalid Input");
        clearExpressionDisplays();
        isResultDisplayed = false;
    }
}

    //ga handle sa buttons pag e press
    private void EnterNumbers(String number) {
    if (isResultDisplayed) {
        jtxtResult.setText(number);  // Replace the result with the new number
        clearExpressionDisplays();  // Clear the postfix and prefix displays
        isResultDisplayed = false;  // Reset the result flag
    } else {
        jtxtResult.setText(jtxtResult.getText() + number); // Append the number
    }
}
    // e clear ang tatlo 
    private void clearExpressionDisplays() {
    infix.setText("");   
    postfix.setText("");  
    prefix.setText("");  
}

    //
    private void processOperator(char operator, Stack<Double> operands) {
        double b = operands.pop();
    double a = operands.pop();
    switch (operator) {
        case '+':
            operands.push(a + b);
            break;
        case '-':
            operands.push(a - b);
            break;
        case '*':
            operands.push(a * b);
            break;
        case '/':
            if (b != 0) {
                operands.push(a / b);
            } else {
                throw new ArithmeticException("Division by zero");
            }
            break;
    }
    }
    
    

    private double evaluateInfix(String infix) {
        Stack<Double> operands = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < infix.length(); i++) {
            char c = infix.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < infix.length() && (Character.isDigit(infix.charAt(i)) || infix.charAt(i) == '.')) {
                    sb.append(infix.charAt(i++));
                }
                i--;
                operands.push(Double.parseDouble(sb.toString()));
            } else if (c == '-' && (i == 0 || isOperator(String.valueOf(infix.charAt(i - 1))) || infix.charAt(i - 1) == '(')) {
                StringBuilder sb = new StringBuilder();
                i++;
                while (i < infix.length() && (Character.isDigit(infix.charAt(i)) || infix.charAt(i) == '.')) {
                    sb.append(infix.charAt(i++));
                }
                i--;
                operands.push(-Double.parseDouble(sb.toString()));
            } else if (isOperator(String.valueOf(c))) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                    processOperator(operators.pop(), operands);
                }
                operators.push(c);
            } else if (c == '(') {
                operators.push(c);
            } else if (c == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    processOperator(operators.pop(), operands);
                }
                operators.pop();
            }
        }

        while (!operators.isEmpty()) {
            processOperator(operators.pop(), operands);
        }

        return operands.pop();
    }

    class ExpressionNode {
    String value;
    ExpressionNode left, right;

    public ExpressionNode(String value) {
        this.value = value;
        this.left = null;
        this.right = null;
    }
}

private ExpressionNode buildExpressionTree(String postfixExpression) {
    Stack<ExpressionNode> stack = new Stack<>();
    for (int i = 0; i < postfixExpression.length(); i++) {
        char c = postfixExpression.charAt(i);

        if (Character.isDigit(c)) {
            stack.push(new ExpressionNode(String.valueOf(c)));
        } else if (isOperator(String.valueOf(c))) {
            ExpressionNode operatorNode = new ExpressionNode(String.valueOf(c));
            operatorNode.right = stack.pop();
            operatorNode.left = stack.pop();
            stack.push(operatorNode);
        }
    }
    return stack.pop();
}

private double evaluateExpressionTree(ExpressionNode root) {
    if (root == null) {
        return 0;
    }
    if (root.left == null && root.right == null) {
        return Double.parseDouble(root.value);
    }

    double leftValue = evaluateExpressionTree(root.left);
    double rightValue = evaluateExpressionTree(root.right);

    switch (root.value) {
        case "+":
            return leftValue + rightValue;
        case "-":
            return leftValue - rightValue;
        case "*":
            return leftValue * rightValue;
        case "/":
            if (rightValue != 0) {
                return leftValue / rightValue;
            } else {
                throw new ArithmeticException("Division by zero");
            }
    }
    return 0;
}

private int precedence(char operator) {
    switch (operator) {
        case '+':
        case '-':
            return 1;
        case '*':
        case '/':
            return 2;
        default:
            return 0;
    }
}

private String infixToPostfix(String infix) {
    Stack<Character> operators = new Stack<>();
    StringBuilder postfix = new StringBuilder();
    boolean lastWasOperator = true; 

    for (int i = 0; i < infix.length(); i++) {
        char c = infix.charAt(i);

        if (Character.isDigit(c) || c == '.') {
            postfix.append(c);
            lastWasOperator = false; 
        } else if (c == '-' && (i == 0 || lastWasOperator || infix.charAt(i - 1) == '(')) {
    
            postfix.append(c); 
            lastWasOperator = false; 
        } else if (c == '(') {
            operators.push(c);
            lastWasOperator = true;
        } else if (c == ')') {
            while (!operators.isEmpty() && operators.peek() != '(') {
                postfix.append(operators.pop());
            }
            operators.pop(); 
            lastWasOperator = false;
        } else if (isOperator(String.valueOf(c))) {
            postfix.append(' '); 
            while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                postfix.append(operators.pop());
            }
            operators.push(c);
            lastWasOperator = true;
        }
    }

    while (!operators.isEmpty()) {
        postfix.append(operators.pop());
    }

    return postfix.toString();
}
private String infixToPrefix(String infix) {
    Stack<String> operators = new Stack<>();
    Stack<String> operands = new Stack<>();

    for (int i = 0; i < infix.length(); i++) {
        char c = infix.charAt(i);

        if (Character.isDigit(c) || c == '.') {

            StringBuilder sb = new StringBuilder();
            while (i < infix.length() && (Character.isDigit(infix.charAt(i)) || infix.charAt(i) == '.')) {
                sb.append(infix.charAt(i));
                i++;
            }
            i--; 
            operands.push(sb.toString());
        } else if (c == '(') {
            operators.push(String.valueOf(c));
        } else if (c == ')') {
            while (!operators.isEmpty() && !operators.peek().equals("(")) {
                String operator = operators.pop();
                String operand2 = operands.pop();
                String operand1 = operands.pop();
                operands.push(operator + " " + operand1 + " " + operand2);
            }
            operators.pop();
        } else if (isOperator(String.valueOf(c))) {
       
            if (c == '-' && (i == 0 || infix.charAt(i - 1) == '(' || isOperator(String.valueOf(infix.charAt(i - 1))))) {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                i++;
                while (i < infix.length() && (Character.isDigit(infix.charAt(i)) || infix.charAt(i) == '.')) {
                    sb.append(infix.charAt(i));
                    i++;
                }
                i--;
                operands.push(sb.toString());
                continue;
            }

            while (!operators.isEmpty() && precedence(operators.peek().charAt(0)) >= precedence(c)) {
                String operator = operators.pop();
                String operand2 = operands.pop();
                String operand1 = operands.pop();
                operands.push(operator + " " + operand1 + " " + operand2);
            }
            operators.push(String.valueOf(c));
        }
    }

    while (!operators.isEmpty()) {
        String operator = operators.pop();
        String operand2 = operands.pop();
        String operand1 = operands.pop();
        operands.push(operator + " " + operand1 + " " + operand2);
    }

    return operands.pop();
}

    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/");
    }

    private boolean isResultDisplayed = false;

    
    
    private boolean isValidExpression(String expression) {
    if (expression.matches(".*[\\+\\-\\*/]{2,}.*")) {
        return false;
    }
    int openParen = 0;
    for (char c : expression.toCharArray()) {
        if (c == '(') openParen++;
        if (c == ')') openParen--;
        if (openParen < 0) return false; 
    }
    return openParen == 0;  
}
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jtxtResult = new javax.swing.JTextField();
        jtxtLiveResult = new javax.swing.JTextField();
        jbtnPlus = new javax.swing.JButton();
        jbtnDigit7 = new javax.swing.JButton();
        jbtnDigit8 = new javax.swing.JButton();
        jbtnDigit9 = new javax.swing.JButton();
        jbtnBS = new javax.swing.JButton();
        jbtnC = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jbtnDigit2 = new javax.swing.JButton();
        jbtnDigit3 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jbtnDigit4 = new javax.swing.JButton();
        jbtnDigit5 = new javax.swing.JButton();
        jbtnDigit6 = new javax.swing.JButton();
        jButton25 = new javax.swing.JButton();
        jbtnDot = new javax.swing.JButton();
        jButton28 = new javax.swing.JButton();
        jbtnDigit10 = new javax.swing.JButton();
        postfix = new javax.swing.JTextField();
        infix = new javax.swing.JTextField();
        prefix = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jbtnDigit11 = new javax.swing.JButton();
        jbtnDigit12 = new javax.swing.JButton();
        jToggleButton1 = new javax.swing.JToggleButton();
        jToggleButton2 = new javax.swing.JToggleButton();

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jtxtResult.setEditable(false);
        jtxtResult.setBackground(new java.awt.Color(51, 51, 51));
        jtxtResult.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jtxtResult.setForeground(new java.awt.Color(211, 211, 211));
        jtxtResult.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtResult.setBorder(null);
        jtxtResult.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtResultActionPerformed(evt);
            }
        });
        getContentPane().add(jtxtResult, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 320, 40));

        jtxtLiveResult.setEditable(false);
        jtxtLiveResult.setBackground(new java.awt.Color(51, 51, 51));
        jtxtLiveResult.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jtxtLiveResult.setForeground(new java.awt.Color(211, 211, 211));
        jtxtLiveResult.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtLiveResult.setBorder(null);
        jtxtLiveResult.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtLiveResultActionPerformed(evt);
            }
        });
        getContentPane().add(jtxtLiveResult, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 320, 40));

        jbtnPlus.setBackground(new java.awt.Color(0, 123, 255));
        jbtnPlus.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jbtnPlus.setText("+");
        jbtnPlus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnPlusActionPerformed(evt);
            }
        });
        getContentPane().add(jbtnPlus, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 220, 74, 70));

        jbtnDigit7.setBackground(new java.awt.Color(128, 128, 128));
        jbtnDigit7.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jbtnDigit7.setText("7");
        jbtnDigit7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDigit7ActionPerformed(evt);
            }
        });
        getContentPane().add(jbtnDigit7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 300, 74, 70));

        jbtnDigit8.setBackground(new java.awt.Color(128, 128, 128));
        jbtnDigit8.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jbtnDigit8.setText("8");
        jbtnDigit8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDigit8ActionPerformed(evt);
            }
        });
        getContentPane().add(jbtnDigit8, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 300, 74, 70));

        jbtnDigit9.setBackground(new java.awt.Color(128, 128, 128));
        jbtnDigit9.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jbtnDigit9.setText("9");
        jbtnDigit9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDigit9ActionPerformed(evt);
            }
        });
        getContentPane().add(jbtnDigit9, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 300, 74, 70));

        jbtnBS.setBackground(new java.awt.Color(255, 165, 0));
        jbtnBS.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jbtnBS.setText("←");
        jbtnBS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnBSActionPerformed(evt);
            }
        });
        getContentPane().add(jbtnBS, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 220, 74, 70));

        jbtnC.setBackground(new java.awt.Color(255, 77, 77));
        jbtnC.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jbtnC.setText("C");
        jbtnC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCActionPerformed(evt);
            }
        });
        getContentPane().add(jbtnC, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 220, 74, 70));

        jButton17.setBackground(new java.awt.Color(0, 123, 255));
        jButton17.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jButton17.setText("*");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton17, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 300, 74, 70));

        jButton18.setBackground(new java.awt.Color(128, 128, 128));
        jButton18.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jButton18.setText("1");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton18, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 460, 74, 70));

        jbtnDigit2.setBackground(new java.awt.Color(128, 128, 128));
        jbtnDigit2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jbtnDigit2.setText("2");
        jbtnDigit2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDigit2ActionPerformed(evt);
            }
        });
        getContentPane().add(jbtnDigit2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 460, 74, 70));

        jbtnDigit3.setBackground(new java.awt.Color(128, 128, 128));
        jbtnDigit3.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jbtnDigit3.setText("3");
        jbtnDigit3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDigit3ActionPerformed(evt);
            }
        });
        getContentPane().add(jbtnDigit3, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 460, 74, 70));

        jButton21.setBackground(new java.awt.Color(0, 123, 255));
        jButton21.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jButton21.setText("-");
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton21, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 220, 74, 70));

        jbtnDigit4.setBackground(new java.awt.Color(128, 128, 128));
        jbtnDigit4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jbtnDigit4.setText("4");
        jbtnDigit4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDigit4ActionPerformed(evt);
            }
        });
        getContentPane().add(jbtnDigit4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 380, 74, 70));

        jbtnDigit5.setBackground(new java.awt.Color(128, 128, 128));
        jbtnDigit5.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jbtnDigit5.setText("5");
        jbtnDigit5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDigit5ActionPerformed(evt);
            }
        });
        getContentPane().add(jbtnDigit5, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 380, 74, 70));

        jbtnDigit6.setBackground(new java.awt.Color(128, 128, 128));
        jbtnDigit6.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jbtnDigit6.setText("6");
        jbtnDigit6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDigit6ActionPerformed(evt);
            }
        });
        getContentPane().add(jbtnDigit6, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 380, 74, 70));

        jButton25.setBackground(new java.awt.Color(0, 123, 255));
        jButton25.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jButton25.setText("/");
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton25, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 380, 74, 70));

        jbtnDot.setBackground(new java.awt.Color(169, 169, 169));
        jbtnDot.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jbtnDot.setText(".");
        jbtnDot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDotActionPerformed(evt);
            }
        });
        getContentPane().add(jbtnDot, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 540, 74, 70));

        jButton28.setBackground(new java.awt.Color(0, 123, 255));
        jButton28.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jButton28.setText("=");
        jButton28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton28ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton28, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 460, 74, 70));

        jbtnDigit10.setBackground(new java.awt.Color(128, 128, 128));
        jbtnDigit10.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jbtnDigit10.setText("0");
        jbtnDigit10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDigit10ActionPerformed(evt);
            }
        });
        getContentPane().add(jbtnDigit10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 540, 74, 70));

        postfix.setEditable(false);
        postfix.setBackground(new java.awt.Color(51, 51, 51));
        postfix.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        postfix.setForeground(new java.awt.Color(211, 211, 211));
        postfix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                postfixActionPerformed(evt);
            }
        });
        getContentPane().add(postfix, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 150, 260, -1));

        infix.setEditable(false);
        infix.setBackground(new java.awt.Color(51, 51, 51));
        infix.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        infix.setForeground(new java.awt.Color(211, 211, 211));
        infix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infixActionPerformed(evt);
            }
        });
        getContentPane().add(infix, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 120, 260, -1));

        prefix.setEditable(false);
        prefix.setBackground(new java.awt.Color(51, 51, 51));
        prefix.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        prefix.setForeground(new java.awt.Color(211, 211, 211));
        prefix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prefixActionPerformed(evt);
            }
        });
        getContentPane().add(prefix, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 180, 260, -1));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("POSTFIX");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, -1, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("INFIX");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("PREFIX");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 180, -1, -1));

        jbtnDigit11.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jbtnDigit11.setText("(");
        jbtnDigit11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDigit11ActionPerformed(evt);
            }
        });
        getContentPane().add(jbtnDigit11, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 540, 74, 70));

        jbtnDigit12.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jbtnDigit12.setText(")");
        jbtnDigit12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDigit12ActionPerformed(evt);
            }
        });
        getContentPane().add(jbtnDigit12, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 540, 74, 70));

        jToggleButton1.setBackground(new java.awt.Color(255, 0, 0));
        jToggleButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jToggleButton1.setText("X");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jToggleButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 10, -1, -1));

        jToggleButton2.setBackground(new java.awt.Color(255, 195, 0));
        jToggleButton2.setText("-");
        jToggleButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jToggleButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 10, -1, -1));

        setSize(new java.awt.Dimension(351, 631));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    
    
    private void jtxtResultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtResultActionPerformed
        // TODO add your handling code hjtxtResultActionPerformedere:
        evaluateExpression();
    }//GEN-LAST:event_jtxtResultActionPerformed

    private void jbtnBSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnBSActionPerformed
        // TODO add your handling code here:
         String currentText = jtxtResult.getText();

    if (!currentText.isEmpty()) {
        currentText = currentText.substring(0, currentText.length() - 1);
        jtxtResult.setText(currentText);

        if (currentText.isEmpty()) {
            clearExpressionDisplays();
        }
    }
    }//GEN-LAST:event_jbtnBSActionPerformed

    private void setOperator(String operator) {
        String currentText = jtxtResult.getText();
    if (currentText.isEmpty() || currentText.endsWith("+") || currentText.endsWith("-") || currentText.endsWith("*") || currentText.endsWith("/")) {
        return;
    }
    jtxtResult.setText(currentText + " " + operator + " ");
    }
    
    private void jbtnDigit7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDigit7ActionPerformed
        // TODO add your handling code here:
        EnterNumbers("7");
    }//GEN-LAST:event_jbtnDigit7ActionPerformed

    private void jbtnPlusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPlusActionPerformed
        // TODO add your handling code here:
        if (isResultDisplayed) {
        String currentResult = jtxtResult.getText().trim();
        jtxtResult.setText(currentResult + " + "); 
        clearExpressionDisplays();
        isResultDisplayed = false;
    } else {
        String currentText = jtxtResult.getText().trim();

        if (!currentText.isEmpty() && !currentText.matches(".*[+\\-*/] ?$")) {
            jtxtResult.setText(currentText + " + ");
        }
    }
    }//GEN-LAST:event_jbtnPlusActionPerformed

    private void jbtnDigit8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDigit8ActionPerformed
        // TODO add your handling code here:
        EnterNumbers("8");

    }//GEN-LAST:event_jbtnDigit8ActionPerformed

    private void jbtnDigit9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDigit9ActionPerformed
        // TODO add your handling code here:
        EnterNumbers("9");

    }//GEN-LAST:event_jbtnDigit9ActionPerformed

    private void jbtnDigit4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDigit4ActionPerformed
        // TODO add your handling code here:
        EnterNumbers("4");

    }//GEN-LAST:event_jbtnDigit4ActionPerformed

    private void jbtnDigit5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDigit5ActionPerformed
        // TODO add your handling code here:
        EnterNumbers("5");

    }//GEN-LAST:event_jbtnDigit5ActionPerformed

    private void jbtnDigit6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDigit6ActionPerformed
        // TODO add your handling code here:
        EnterNumbers("6");
    }//GEN-LAST:event_jbtnDigit6ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        // TODO add your handling code here:
        EnterNumbers("1");

    }//GEN-LAST:event_jButton18ActionPerformed

    private void jbtnDigit2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDigit2ActionPerformed
        // TODO add your handling code here:
        EnterNumbers("2");

    }//GEN-LAST:event_jbtnDigit2ActionPerformed

    private void jbtnDigit3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDigit3ActionPerformed
        // TODO add your handling code here:
        EnterNumbers("3");

    }//GEN-LAST:event_jbtnDigit3ActionPerformed

    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        // TODO add your handling code here:
        if (isResultDisplayed) {
        String currentResult = jtxtResult.getText().trim();
        jtxtResult.setText(currentResult + " / "); 
        clearExpressionDisplays();
        isResultDisplayed = false;
    } else {
        String currentText = jtxtResult.getText().trim();

        if (!currentText.isEmpty() && !currentText.matches(".*[+\\-*/] ?$")) {
            jtxtResult.setText(currentText + " / ");
        }
    }  
    }//GEN-LAST:event_jButton25ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        // TODO add your handling code here:
        if (isResultDisplayed) {
        String currentResult = jtxtResult.getText().trim();
        jtxtResult.setText(currentResult + " * "); 
        clearExpressionDisplays();
        isResultDisplayed = false;
    } else {
        String currentText = jtxtResult.getText().trim();

        if (!currentText.isEmpty() && !currentText.matches(".*[+\\-*/] ?$")) {
            jtxtResult.setText(currentText + " * ");
        }
    }  
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        // TODO add your handling code here:
        String currentText = jtxtResult.getText().trim();

    if (isResultDisplayed) {
        jtxtResult.setText(currentText + " - ");
        isResultDisplayed = false; 
    } else {
        if (currentText.isEmpty()) {
            jtxtResult.setText("-"); 
        } else {
            if (isOperator(String.valueOf(currentText.charAt(currentText.length() - 1))) || 
                currentText.charAt(currentText.length() - 1) == '(') {
                jtxtResult.setText(currentText + "(-");
            } 
            else if (Character.isDigit(currentText.charAt(currentText.length() - 1)) || 
                     currentText.charAt(currentText.length() - 1) == ')') {
                jtxtResult.setText(currentText + " - ");
            }
            else if (currentText.charAt(currentText.length() - 1) == '-') {
                return;
            }
            else {
                jtxtResult.setText(currentText + " - ");
            }
        }
    }
    }//GEN-LAST:event_jButton21ActionPerformed

    private void jbtnCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCActionPerformed
        // TODO add your handling code here:
        jtxtResult.setText("");
        jtxtLiveResult.setText("");
        infix.setText("");
        prefix.setText("");
        postfix.setText("");
    }//GEN-LAST:event_jbtnCActionPerformed

    private void jbtnDotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDotActionPerformed
        // TODO add your handling code here:
        String currentText = jtxtResult.getText();
     if (currentText.isEmpty() || currentText.endsWith(" ")) {

         jtxtResult.setText(currentText + "0.");
     } else {
         String[] parts = currentText.split(" ");
         String lastPart = parts[parts.length - 1];
         if (!lastPart.contains(".")) {
             jtxtResult.setText(currentText + ".");
         }
     }
    }//GEN-LAST:event_jbtnDotActionPerformed

    private void jButton28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton28ActionPerformed
            // TODO add your handling code here:
            evaluateExpression();

    jtxtLiveResult.setText("");

    }//GEN-LAST:event_jButton28ActionPerformed

    private void jbtnDigit10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDigit10ActionPerformed
        // TODO add your handling code here:
        EnterNumbers("0");
    }//GEN-LAST:event_jbtnDigit10ActionPerformed

    private void infixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infixActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_infixActionPerformed

    private void prefixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prefixActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_prefixActionPerformed

    private void postfixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_postfixActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_postfixActionPerformed

    private void jbtnDigit11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDigit11ActionPerformed
        // TODO add your handling code here:
        EnterNumbers("(");
    }//GEN-LAST:event_jbtnDigit11ActionPerformed

    private void jbtnDigit12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDigit12ActionPerformed
        // TODO add your handling code here:
        EnterNumbers(")");
    }//GEN-LAST:event_jbtnDigit12ActionPerformed

    private void jtxtLiveResultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtLiveResultActionPerformed
        // TODO add your handling code here:
        try {
        String currentExpression = jtxtResult.getText().trim();

        // Check if the input contains an operator
        if (containsOperator(currentExpression)) {
            // Temporarily update the jtxtResult to evaluate the expression
            evaluateExpression();

            // Retrieve the evaluated result
            String liveResult = jtxtResult.getText();

            // Display the live result in jtxtLiveResult
            jtxtLiveResult.setText(liveResult);
        } else {
            // If there's no operator, clear the live result
            jtxtLiveResult.setText("");
        }
    } catch (Exception e) {
        jtxtLiveResult.setText("Error");
    }
}


private boolean containsOperator(String expression) {
    return expression.contains("+") || expression.contains("-") || 
           expression.contains("*") || expression.contains("/");
    }//GEN-LAST:event_jtxtLiveResultActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        // TODO add your handling code here:
        positionX = evt.getX();
        positionY = evt.getY();
    }//GEN-LAST:event_formMousePressed

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        // TODO add your handling code here:
        int x = evt.getXOnScreen();  
        int y = evt.getYOnScreen(); 
        setLocation(x - positionX, y - positionY);
    }//GEN-LAST:event_formMouseDragged

    private void jToggleButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton2ActionPerformed
        // TODO add your handling code here:
        this.setState(javax.swing.JFrame.ICONIFIED);

    }//GEN-LAST:event_jToggleButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Calculator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Calculator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Calculator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Calculator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Calculator().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField infix;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton28;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JButton jbtnBS;
    private javax.swing.JButton jbtnC;
    private javax.swing.JButton jbtnDigit10;
    private javax.swing.JButton jbtnDigit11;
    private javax.swing.JButton jbtnDigit12;
    private javax.swing.JButton jbtnDigit2;
    private javax.swing.JButton jbtnDigit3;
    private javax.swing.JButton jbtnDigit4;
    private javax.swing.JButton jbtnDigit5;
    private javax.swing.JButton jbtnDigit6;
    private javax.swing.JButton jbtnDigit7;
    private javax.swing.JButton jbtnDigit8;
    private javax.swing.JButton jbtnDigit9;
    private javax.swing.JButton jbtnDot;
    private javax.swing.JButton jbtnPlus;
    private javax.swing.JTextField jtxtLiveResult;
    private javax.swing.JTextField jtxtResult;
    private javax.swing.JTextField postfix;
    private javax.swing.JTextField prefix;
    // End of variables declaration//GEN-END:variables
}
