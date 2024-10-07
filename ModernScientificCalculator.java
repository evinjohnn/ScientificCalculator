import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Timer;

public class ModernScientificCalculator extends JFrame {
    private JTextField display;
    private JLabel historyLabel;
    private double result = 0;
    private String lastCommand = "=";
    private boolean start = true;
    private JPanel buttonPanel;
    private Color accentColor = new Color(64, 156, 255);
    private boolean isDarkMode = false;
    private Map<JButton, Point> originalButtonPositions = new HashMap<>();
    
    // Animation related fields
    private Timer buttonAnimationTimer;
    private int animationDuration = 200; // milliseconds
    
    public ModernScientificCalculator() {
        setTitle("Scientific Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Display panel
        JPanel displayPanel = new JPanel(new BorderLayout(5, 5));
        displayPanel.setOpaque(false);
        
        historyLabel = new JLabel(" ");
        historyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        historyLabel.setHorizontalAlignment(JLabel.RIGHT);
        displayPanel.add(historyLabel, BorderLayout.NORTH);
        
        display = new JTextField("0");
        display.setEditable(false);
        display.setBorder(null);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setFont(new Font("Segoe UI", Font.BOLD, 32));
        displayPanel.add(display, BorderLayout.CENTER);
        
        add(displayPanel, BorderLayout.NORTH);
        
        // Button panel
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(8, 5, 8, 8));
        
        // Scientific functions
        String[] scientificButtons = {
            "2ⁿᵈ", "x²", "x³", "xʸ", "eˣ",
            "sin", "cos", "tan", "log", "ln",
            "sinh", "cosh", "tanh", "10ˣ", "√",
            "(", ")", "n!", "±", "C",
            "7", "8", "9", "÷", "mod",
            "4", "5", "6", "×", "%",
            "1", "2", "3", "−", "1/x",
            "0", ".", "π", "=", "+"
        };
        
        for (String buttonText : scientificButtons) {
            JButton button = createButton(buttonText);
            buttonPanel.add(button);
            
            // Store original position for animation
            originalButtonPositions.put(button, button.getLocation());
        }
        
        add(buttonPanel, BorderLayout.CENTER);
        
        // Theme toggle button with icon
        JButton themeToggle = new JButton("\uD83C\uDF19"); // Moon emoji
        themeToggle.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        themeToggle.setFocusPainted(false);
        themeToggle.setBorderPainted(false);
        themeToggle.addActionListener(e -> toggleTheme());
        
        // Add theme toggle to a toolbar
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setOpaque(false);
        toolbar.setBorder(null);
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(themeToggle);
        add(toolbar, BorderLayout.SOUTH);
        
        setupButtonAnimations();
        applyTheme();
        setSize(400, 600);
    }
    
    private void setupButtonAnimations() {
        buttonAnimationTimer = new Timer(16, new ActionListener() {
            private long startTime;
            private JButton activeButton;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - startTime;
                float progress = Math.min(1f, elapsed / (float) animationDuration);
                
                if (progress >= 1f) {
                    buttonAnimationTimer.stop();
                    if (activeButton != null) {
                        activeButton.setBackground(getButtonColor());
                    }
                    return;
                }
                
                if (activeButton != null) {
                    Color startColor = getButtonColor();
                    Color endColor = accentColor;
                    Color currentColor = interpolateColor(startColor, endColor, progress);
                    activeButton.setBackground(currentColor);
                }
            }
            
            public void startAnimation(JButton button) {
                this.activeButton = button;
                startTime = System.currentTimeMillis();
                buttonAnimationTimer.start();
            }
        });
    }
    
    private Color interpolateColor(Color c1, Color c2, float fraction) {
        int red = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * fraction);
        int green = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * fraction);
        int blue = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * fraction);
        return new Color(red, green, blue);
    }
    
    private JButton createButton(String label) {
        JButton button = new JButton(label);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.addActionListener(new ButtonClickListener());
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ((Timer)buttonAnimationTimer).getActionListeners()[0].actionPerformed(
                    new ActionEvent(button, ActionEvent.ACTION_PERFORMED, ""));
            }
        });
        return button;
    }
    
    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        applyTheme();
    }
    
    private Color getButtonColor() {
        return isDarkMode ? new Color(60, 60, 60) : new Color(240, 240, 240);
    }
    
    private void applyTheme() {
        Color bgColor = isDarkMode ? new Color(30, 30, 30) : new Color(250, 250, 250);
        Color fgColor = isDarkMode ? Color.WHITE : Color.BLACK;
        Color buttonColor = getButtonColor();
        
        UIManager.put("Button.select", accentColor);
        
        buttonPanel.setBackground(bgColor);
        display.setBackground(bgColor);
        display.setForeground(fgColor);
        display.setCaretColor(fgColor);
        historyLabel.setForeground(isDarkMode ? new Color(180, 180, 180) : new Color(100, 100, 100));
        
        for (Component c : buttonPanel.getComponents()) {
            if (c instanceof JButton) {
                JButton button = (JButton) c;
                button.setBackground(buttonColor);
                button.setForeground(fgColor);
            }
        }
        
        this.getContentPane().setBackground(bgColor);
    }
    
    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String command = event.getActionCommand();
            JButton button = (JButton) event.getSource();
            
            // Start animation
            ((Timer)buttonAnimationTimer).getActionListeners()[0].actionPerformed(
                new ActionEvent(button, ActionEvent.ACTION_PERFORMED, ""));
            
            if (command.matches("[0-9.]")) {
                if (start) {
                    display.setText(command);
                    start = false;
                } else {
                    display.setText(display.getText() + command);
                }
            } else {
                if (start) {
                    if (command.equals("−")) {
                        display.setText(command);
                        start = false;
                    } else {
                        lastCommand = command;
                    }
                } else {
                    calculate(Double.parseDouble(display.getText()));
                    lastCommand = command;
                    start = true;
                }
            }
            
            // Update history
            if (!command.equals("=")) {
                historyLabel.setText(historyLabel.getText() + " " + command);
            } else {
                historyLabel.setText("");
            }
        }
    }
    
    public void calculate(double x) {
        switch (lastCommand) {
            case "+": result += x; break;
            case "−": result -= x; break;
            case "×": result *= x; break;
            case "÷": result /= x; break;
            case "=": result = x; break;
            case "sin": result = Math.sin(Math.toRadians(x)); break;
            case "cos": result = Math.cos(Math.toRadians(x)); break;
            case "tan": result = Math.tan(Math.toRadians(x)); break;
            case "√": result = Math.sqrt(x); break;
            case "log": result = Math.log10(x); break;
            case "ln": result = Math.log(x); break;
            case "x²": result = x * x; break;
            case "x³": result = x * x * x; break;
            case "π": result = Math.PI; break;
            case "±": result = -x; break;
            case "1/x": result = 1 / x; break;
            case "%": result = x / 100; break;
            case "n!": result = factorial((int)x); break;
            case "sinh": result = Math.sinh(x); break;
            case "cosh": result = Math.cosh(x); break;
            case "tanh": result = Math.tanh(x); break;
            case "10ˣ": result = Math.pow(10, x); break;
            case "eˣ": result = Math.exp(x); break;
            case "mod": result %= x; break;
            case "C": result = 0; historyLabel.setText(""); break;
        }
        display.setText(String.format("%.8f", result).replaceAll("\\.?0+$", ""));
    }
    
    private double factorial(int n) {
        if (n == 0) return 1;
        double fact = 1;
        for (int i = 1; i <= n; i++) {
            fact *= i;
        }
        return fact;
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            ModernScientificCalculator calc = new ModernScientificCalculator();
            calc.setVisible(true);
        });
    }
}
