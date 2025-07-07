import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.net.*;
import java.io.*;

public class Client extends JFrame {
    private JPanel chatPanel;
    private JScrollPane scrollPane;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private JTextField messageField;
    private JDialog emojiDialog;
    private DataOutputStream dos;
    private Socket socket;

    Client() {
        setTitle("G Box For G's (Client)");
        setLayout(new BorderLayout());

        // Top panel with BorderLayout
        JPanel p1 = new JPanel(new BorderLayout());
        p1.setBackground(new Color(255, 188, 217));
        p1.setPreferredSize(new Dimension(0, 70));

        // ===== Left side: Back button + Profile pic =====
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        leftPanel.setOpaque(false);

        // Back Button
        ImageIcon backIcon = new ImageIcon("Images/back.png");
        Image backImg = backIcon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        JButton backButton = new JButton(new ImageIcon(backImg));
        backButton.setPreferredSize(new Dimension(45, 45));
        backButton.setBorder(BorderFactory.createEmptyBorder());
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.addActionListener(e -> System.exit(0));

        // Profile Picture
        ImageIcon profileIcon = new ImageIcon("Images/peterrr.png");
        Image profileImg = profileIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        JLabel profileLabel = new JLabel(new ImageIcon(profileImg));
        profileLabel.setPreferredSize(new Dimension(40, 40));

        leftPanel.add(backButton);
        leftPanel.add(profileLabel);

        // Panel for name + status
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);

        // Name
        JLabel nameLabel = new JLabel("Stark Bhayya");
        nameLabel.setFont(new Font("SF Mono", Font.PLAIN, 17));
        nameLabel.setForeground(Color.BLACK);

        // Status
        JLabel statusLabel = new JLabel("Active now");
        statusLabel.setFont(new Font("SF Mono", Font.PLAIN, 13));
        statusLabel.setForeground(new Color(156, 39, 176));

        namePanel.add(nameLabel);
        namePanel.add(statusLabel);

        // Add to the left side of top bar
        leftPanel.add(namePanel);

        // ===== Right side: Voice & Video call buttons =====
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightPanel.setOpaque(false);

        // Voice Call Button
        ImageIcon voiceIcon = new ImageIcon("Images/call-button.png");
        Image voiceImg = voiceIcon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        JButton voiceButton = new JButton(new ImageIcon(voiceImg));
        voiceButton.setPreferredSize(new Dimension(45, 45));
        voiceButton.setBorder(BorderFactory.createEmptyBorder());
        voiceButton.setContentAreaFilled(false);
        voiceButton.setFocusPainted(false);
        voiceButton.setBorderPainted(false);

        // Video Call Button
        ImageIcon videoIcon = new ImageIcon("Images/video.png");
        Image videoImg = videoIcon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        JButton videoButton = new JButton(new ImageIcon(videoImg));
        videoButton.setPreferredSize(new Dimension(45, 45));
        videoButton.setBorder(BorderFactory.createEmptyBorder());
        videoButton.setContentAreaFilled(false);
        videoButton.setFocusPainted(false);
        videoButton.setBorderPainted(false);

        rightPanel.add(voiceButton);
        rightPanel.add(videoButton);

        // Add both sides to top bar
        p1.add(leftPanel, BorderLayout.WEST);
        p1.add(rightPanel, BorderLayout.EAST);

        // Add pink panel to main frame
        add(p1, BorderLayout.NORTH);

        // ===== CHAT AREA =====
        setupChatArea();

        // ===== Bottom Panel =====
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBackground(new Color(240, 242, 245));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        bottomPanel.setPreferredSize(new Dimension(0, 65));

        // Create the input container with rounded background
        JPanel inputContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 25, 25));
                g2d.dispose();
            }
        };
        inputContainer.setLayout(new BorderLayout());
        inputContainer.setOpaque(false);
        inputContainer.setPreferredSize(new Dimension(0, 45));

        // ==== Left: Emoji Button ====
        ImageIcon emojiIcon = new ImageIcon("Images/icons8-poultry-leg-48.png");
        Image emojiImg = emojiIcon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
        JButton emojiButton = new JButton(new ImageIcon(emojiImg));
        emojiButton.setPreferredSize(new Dimension(45, 45));
        emojiButton.setBorder(BorderFactory.createEmptyBorder());
        emojiButton.setContentAreaFilled(false);
        emojiButton.setFocusPainted(false);
        emojiButton.setBorderPainted(false);
        emojiButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // ==== Center: Text Field ====
        messageField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                setOpaque(false);
                super.paintComponent(g);
            }
        };
        messageField.setFont(new Font("Arial", Font.PLAIN, 15));
        messageField.setBorder(BorderFactory.createEmptyBorder(12, 5, 12, 12));
        messageField.setOpaque(false);
        messageField.setBackground(Color.WHITE);
        messageField.setForeground(Color.BLACK);

        // Create a panel to hold emoji button and text field
        JPanel leftInputPanel = new JPanel(new BorderLayout());
        leftInputPanel.setOpaque(false);
        leftInputPanel.add(emojiButton, BorderLayout.WEST);
        leftInputPanel.add(messageField, BorderLayout.CENTER);

        inputContainer.add(leftInputPanel, BorderLayout.CENTER);

        // ==== Send Button ====
        ImageIcon sendIconOriginal = new ImageIcon("Images/fast-forward.png");
        Image sendImgScaled = sendIconOriginal.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        
        JButton sendButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw circular background
                g2d.setColor(new Color(37, 211, 102));
                g2d.fillOval(2, 2, getWidth() - 4, getHeight() - 4);
                
                // Draw the send icon
                if (sendIconOriginal.getIconWidth() > 0) {
                    g2d.drawImage(sendImgScaled, 
                                 (getWidth() - 20) / 2, 
                                 (getHeight() - 20) / 2, 
                                 null);
                } else {
                    // Fallback
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new BasicStroke(2));
                    int centerX = getWidth() / 2;
                    int centerY = getHeight() / 2;
                    g2d.drawLine(centerX - 6, centerY, centerX + 6, centerY);
                    g2d.drawLine(centerX + 2, centerY - 4, centerX + 6, centerY);
                    g2d.drawLine(centerX + 2, centerY + 4, centerX + 6, centerY);
                }
                
                g2d.dispose();
            }
        };
        sendButton.setPreferredSize(new Dimension(45, 45));
        sendButton.setBorder(BorderFactory.createEmptyBorder());
        sendButton.setContentAreaFilled(false);
        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Layout the bottom panel
        bottomPanel.add(inputContainer, BorderLayout.CENTER);
        
        // Add some spacing between input and send button
        JPanel sendButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        sendButtonPanel.setOpaque(false);
        sendButtonPanel.add(sendButton);
        bottomPanel.add(sendButtonPanel, BorderLayout.EAST);

        // ==== Action Listeners ====
        sendButton.addActionListener(e -> {
            String text = messageField.getText().trim();
            if (!text.isEmpty()) {
                addMessage(text, true);
                try {
                    if (dos != null) {
                        dos.writeUTF(text);
                        dos.flush();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                messageField.setText("");
            }
        });
        
        messageField.addActionListener(e -> sendButton.doClick());
        
        // Initialize emoji dialog
        setupEmojiPicker(emojiButton);

        // Add to frame
        add(bottomPanel, BorderLayout.SOUTH);

        getContentPane().setBackground(Color.WHITE);
        setSize(450, 950);
        setLocation(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        
        // Connect to server in background thread
        new Thread(() -> connectToServer()).start();
    }

    private void connectToServer() {
        try {
            System.out.println("Connecting to server...");
            socket = new Socket("localhost", 6001);
            System.out.println("Connected to server!");
            
            // Set up output stream for sending messages
            dos = new DataOutputStream(socket.getOutputStream());
            
            // Set up input stream for receiving messages
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            
            // Start message receiver thread
            new Thread(() -> {
                try {
                    while (true) {
                        String msg = dis.readUTF();
                        SwingUtilities.invokeLater(() -> addMessage(msg, false));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupEmojiPicker(JButton emojiButton) {
        // Create the emoji dialog
        emojiDialog = new JDialog(this, "Emoji Picker", false);
        emojiDialog.setUndecorated(true);
        emojiDialog.setSize(300, 250);
        emojiDialog.getContentPane().setBackground(new Color(240, 240, 240));
        emojiDialog.setLayout(new BorderLayout());
        emojiDialog.setFocusableWindowState(true);
        
        // Create rounded border
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2d.dispose();
            }
        };
        contentPanel.setBackground(new Color(245, 245, 245));
        contentPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        contentPanel.setLayout(new BorderLayout());
        
        // Create scrollable emoji panel
        JPanel emojiPanel = new JPanel();
        emojiPanel.setBackground(new Color(245, 245, 245));
        emojiPanel.setLayout(new GridLayout(0, 8, 5, 5)); // 8 columns, 5px spacing
        
        // Popular emoji categories (macOS style)
        String[] emojiCategories = {
            // Smileys & People
            "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😊", "😇", "🙂", "🙃", "😉", "😌", "😍", "🥰",
            "😘", "😗", "😙", "😚", "😋", "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🤩", "🥳", "😏",
            "😒", "😞", "😔", "😟", "😕", "🙁", "☹️", "😣", "😖", "😫", "😩", "🥺", "😢", "😭", "😤", "😠",
            "😡", "🤬", "🤯", "😳", "🥵", "🥶", "😱", "😨", "😰", "😥", "😓", "🤗", "🤔", "🤭", "🤫", "🤥",
            "😶", "😐", "😑", "😬", "🙄", "😯", "😦", "😧", "😮", "😲", "🥱", "😴", "🤤", "😪", "😵", "🤐",
            "🥴", "🤢", "🤮", "🤧", "😷", "🤒", "🤕", "🤑", "🤠", "😈", "👿", "👹", "👺", "🤡", "💩", "👻",
            "💀", "☠️", "👽", "👾", "🤖", "🎃", "😺", "😸", "😹", "😻", "😼", "😽", "🙀", "😿", "😾",
            
            // Animals & Nature
            "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯", "🦁", "🐮", "🐷", "🐽", "🐸", "🐵",
            "🙈", "🙉", "🙊", "🐒", "🐔", "🐧", "🐦", "🐤", "🐣", "🐥", "🦆", "🦅", "🦉", "🦇", "🐺", "🐗",
            "🐴", "🦄", "🐝", "🐛", "🦋", "🐌", "🐞", "🐜", "🦟", "🦗", "🕷", "🦂", "🐢", "🐍", "🦎", "🦖",
            "🦕", "🐙", "🦑", "🦐", "🦞", "🦀", "🐡", "🐠", "🐟", "🐬", "🐳", "🐋", "🦈", "🐊", "🐅", "🐆",
            "🦓", "🦍", "🦧", "🦣", "🐘", "🦛", "🦏", "🐪", "🐫", "🦒", "🦘", "🦬", "🐃", "🐂", "🐄", "🐎",
            "🐖", "🐏", "🐑", "🦙", "🐐", "🦌", "🐕", "🐩", "🦮", "🐕‍🦺", "🐈", "🐈‍⬛", "🐓", "🦃", "🦤", "🦚",
            "🦜", "🦢", "🦩", "🕊", "🐇", "🦝", "🦨", "🦡", "🦫", "🦦", "🦥", "🐁", "🐀", "🐿", "🦔",
            
            // Food & Drink
            "🍏", "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐", "🍈", "🍒", "🍑", "🥭", "🍍", "🥥",
            "🥝", "🍅", "🍆", "🥑", "🥦", "🥬", "🥒", "🌶", "🫑", "🌽", "🥕", "🫒", "🧄", "🧅", "🥔", "🍠",
            "🥐", "🥯", "🍞", "🥖", "🥨", "🧀", "🥚", "🍳", "🧈", "🥞", "🧇", "🥓", "🥩", "🍗", "🍖", "🦴",
            "🌭", "🍔", "🍟", "🍕", "🫓", "🥪", "🥙", "🧆", "🌮", "🌯", "🫔", "🥗", "🥘", "🫕", "🥫", "🍝",
            "🍜", "🍲", "🍛", "🍣", "🍱", "🥟", "🦪", "🍤", "🍙", "🍚", "🍘", "🍥", "🥠", "🥮", "🍢", "🍡",
            "🍧", "🍨", "🍦", "🥧", "🧁", "🍰", "🎂", "🍮", "🍭", "🍬", "🍫", "🍿", "🍩", "🍪", "🌰", "🥜",
            "🍯", "🥛", "🍼", "🫖", "☕", "🍵", "🧃", "🥤", "🧋", "🍶", "🍺", "🍻", "🥂", "🍷", "🥃", "🍸",
            "🍹", "🧉", "🍾", "🧊", "🥄", "🍴", "🍽", "🥣", "🥡", "🥢", "🧂"
        };

        // Add emojis to the panel
        for (String emoji : emojiCategories) {
            JButton emojiBtn = new JButton(emoji);
            emojiBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
            emojiBtn.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            emojiBtn.setContentAreaFilled(false);
            emojiBtn.setFocusPainted(false);
            emojiBtn.addActionListener(e -> {
                messageField.setText(messageField.getText() + emoji);
                messageField.requestFocus();
            });
            emojiBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            emojiPanel.add(emojiBtn);
        }
        
        // Create scroll pane for emojis
        JScrollPane emojiScroll = new JScrollPane(emojiPanel);
        emojiScroll.setBorder(BorderFactory.createEmptyBorder());
        emojiScroll.getVerticalScrollBar().setUnitIncrement(12);
        
        // Add search field at the top
        JTextField searchField = new JTextField();
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.addActionListener(e -> filterEmojis(searchField.getText().trim(), emojiPanel));
        searchField.putClientProperty("JTextField.placeholderText", "Search emojis...");
        
        // Search panel with rounded corners
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        searchPanel.setBackground(new Color(245, 245, 245));
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        // Close button
        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font("Arial", Font.BOLD, 18));
        closeButton.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        closeButton.setContentAreaFilled(false);
        closeButton.setForeground(Color.GRAY);
        closeButton.addActionListener(e -> emojiDialog.setVisible(false));
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.add(searchPanel, BorderLayout.CENTER);
        headerPanel.add(closeButton, BorderLayout.EAST);
        
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(emojiScroll, BorderLayout.CENTER);
        emojiDialog.add(contentPanel);
        
        // Add listener to show emoji dialog
        emojiButton.addActionListener(e -> {
            // Position dialog above emoji button
            Point buttonLoc = emojiButton.getLocationOnScreen();
            int x = buttonLoc.x - emojiDialog.getWidth() + emojiButton.getWidth();
            int y = buttonLoc.y - emojiDialog.getHeight() - 10;
            emojiDialog.setLocation(x, y);
            emojiDialog.setVisible(true);
        });
        
        // Close dialog when clicking outside
        emojiDialog.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                emojiDialog.setVisible(false);
            }
        });
    }
    
    private void filterEmojis(String query, JPanel emojiPanel) {
        Component[] components = emojiPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                String emoji = btn.getText();
                // Simple filter - in real app you'd use emoji names
                boolean matches = emoji.contains(query) || 
                                 (query.length() > 0 && emoji.startsWith(query));
                btn.setVisible(matches);
            }
        }
        emojiPanel.revalidate();
        emojiPanel.repaint();
    }

    private void setupChatArea() {
        // Create main chat panel
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(new Color(230, 221, 212));
        chatPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Create scroll pane
        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void addMessage(String messageText, boolean isSent) {
        // Create message container panel
        JPanel messageContainer = new JPanel(new FlowLayout(
            isSent ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 0));
        messageContainer.setOpaque(false);
        messageContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Create message bubble panel
        JPanel messageBubble = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Different colors for sent vs received
                Color bubbleColor = isSent ? 
                    new Color(220, 248, 198) : // Sent message color (green)
                    Color.WHITE; // Received message color
                
                g2d.setColor(bubbleColor);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
                
                g2d.dispose();
            }
        };
        messageBubble.setOpaque(false);
        messageBubble.setLayout(new BorderLayout());
        messageBubble.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        // Create message content panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 0));
        contentPanel.setOpaque(false);

        // Message text with compact spacing
        JLabel messageLabel = new JLabel("<html><div style='width: 200px; word-wrap: break-word; margin:0; padding:0; line-height:1.3;'>" + messageText + "</div></html>");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setVerticalAlignment(JLabel.TOP);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Time and status panel
        JPanel timeStatusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        timeStatusPanel.setOpaque(false);
        timeStatusPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Current time
        String currentTime = timeFormat.format(new Date());
        JLabel timeLabel = new JLabel(currentTime);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        timeLabel.setForeground(new Color(119, 140, 142));

        // Only show ticks for sent messages
        if (isSent) {
            // Create custom tick marks
            JLabel tickLabel = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(75, 163, 255));
                    g2d.setStroke(new BasicStroke(1.5f));
                    
                    // First tick
                    g2d.drawLine(2, 6, 4, 8);
                    g2d.drawLine(4, 8, 8, 4);
                    
                    // Second tick
                    g2d.drawLine(5, 6, 7, 8);
                    g2d.drawLine(7, 8, 11, 4);
                    
                    g2d.dispose();
                }
            };
            tickLabel.setPreferredSize(new Dimension(15, 12));
            timeStatusPanel.add(tickLabel);
        }

        timeStatusPanel.add(timeLabel);

        // Add components to content panel
        contentPanel.add(messageLabel, BorderLayout.CENTER);
        contentPanel.add(timeStatusPanel, BorderLayout.SOUTH);

        messageBubble.add(contentPanel, BorderLayout.CENTER);

        // Set maximum width for message bubble
        messageBubble.setMaximumSize(new Dimension(280, Integer.MAX_VALUE));
        messageBubble.setPreferredSize(new Dimension(Math.min(280, messageBubble.getPreferredSize().width), messageBubble.getPreferredSize().height));

        messageContainer.add(messageBubble);

        // Add message to chat panel
        chatPanel.add(messageContainer);
        chatPanel.add(Box.createVerticalStrut(2)); // Reduced spacing between messages

        // Auto-scroll to bottom
        SwingUtilities.invokeLater(() -> {
            chatPanel.revalidate();
            chatPanel.repaint();
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Client());
    }
}