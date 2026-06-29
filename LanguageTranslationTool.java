import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

public class LanguageTranslationTool extends JFrame {

    JLabel titleLabel;
    JLabel sourceLabel;
    JLabel targetLabel;

    JComboBox<String> sourceLanguage;
    JComboBox<String> targetLanguage;

    JTextArea inputArea;
    JTextArea outputArea;

    JButton translateButton;
    JButton copyButton;
    JButton clearButton;
    JButton swapButton;

    JPanel topPanel;
    JPanel centerPanel;
    JPanel bottomPanel;

    Font titleFont = new Font("Arial", Font.BOLD, 24);
    Font normalFont = new Font("Arial", Font.PLAIN, 16);

    String[] languages = {
            "English",
            "Hindi",
            "French",
            "German",
            "Spanish",
            "Italian",
            "Russian",
            "Chinese",
            "Japanese",
            "Korean",
            "Arabic"
    };
    private Map<String, String> languageCodes = new HashMap<>();

    public LanguageTranslationTool() {

        setTitle("Language Translation Tool");
        setSize(900,650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        buildTopPanel();
        buildCenterPanel();
        buildBottomPanel();

        initializeLanguageCodes();
        initializeEvents();

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
    private void initializeLanguageCodes() {

        languageCodes.put("English","en");
        languageCodes.put("Hindi","hi");
        languageCodes.put("French","fr");
        languageCodes.put("German","de");
        languageCodes.put("Spanish","es");
        languageCodes.put("Italian","it");
        languageCodes.put("Russian","ru");
        languageCodes.put("Chinese","zh");
        languageCodes.put("Japanese","ja");
        languageCodes.put("Korean","ko");
        languageCodes.put("Arabic","ar");
    }

    private void initializeEvents() {

        translateButton.addActionListener(e -> translateText());

        clearButton.addActionListener(e -> {
            inputArea.setText("");
            outputArea.setText("");
        });

        copyButton.addActionListener(e -> {

            StringSelection selection =
                    new StringSelection(outputArea.getText());

            Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .setContents(selection,null);

            JOptionPane.showMessageDialog(this,
                    "Copied Successfully!");

        });

        swapButton.addActionListener(e -> {

            int s = sourceLanguage.getSelectedIndex();

            sourceLanguage.setSelectedIndex(
                    targetLanguage.getSelectedIndex());

            targetLanguage.setSelectedIndex(s);

            String temp = inputArea.getText();

            inputArea.setText(outputArea.getText());

            outputArea.setText(temp);

        });

    }

    private void buildTopPanel(){

        topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(20,20,20,20));

        titleLabel = new JLabel("Language Translation Tool");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(titleFont);

        JPanel comboPanel = new JPanel(new GridLayout(2,2,10,10));

        sourceLabel = new JLabel("Source Language");
        targetLabel = new JLabel("Target Language");

        sourceLanguage = new JComboBox<>(languages);
        targetLanguage = new JComboBox<>(languages);

        comboPanel.add(sourceLabel);
        comboPanel.add(targetLabel);
        comboPanel.add(sourceLanguage);
        comboPanel.add(targetLanguage);

        topPanel.add(titleLabel,BorderLayout.NORTH);
        topPanel.add(comboPanel,BorderLayout.SOUTH);

    }

    private void buildCenterPanel(){

        centerPanel = new JPanel(new GridLayout(1,2,20,20));
        centerPanel.setBorder(new EmptyBorder(20,20,20,20));

        inputArea = new JTextArea();
        outputArea = new JTextArea();

        outputArea.setEditable(false);

        centerPanel.add(new JScrollPane(inputArea));
        centerPanel.add(new JScrollPane(outputArea));

    }

    private void buildBottomPanel(){

        bottomPanel = new JPanel();

        translateButton = new JButton("Translate");
        copyButton = new JButton("Copy");
        clearButton = new JButton("Clear");
        swapButton = new JButton("Swap");

        bottomPanel.add(translateButton);
        bottomPanel.add(copyButton);
        bottomPanel.add(clearButton);
        bottomPanel.add(swapButton);

    }

    private void translateText() {

        try {

            String text = inputArea.getText().trim();

            if(text.isEmpty()){

                JOptionPane.showMessageDialog(this,
                        "Please enter some text.");

                return;
            }

            String source =
                    languageCodes.get(
                            sourceLanguage.getSelectedItem().toString());

            String target =
                    languageCodes.get(
                            targetLanguage.getSelectedItem().toString());

            String url =
                    "https://api.mymemory.translated.net/get?q="
                            + URLEncoder.encode(text,
                            StandardCharsets.UTF_8)
                            + "&langpair="
                            + source + "|" + target;

            HttpClient client =
                    HttpClient.newHttpClient();

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .GET()
                            .build();

            HttpResponse<String> response =
                    client.send(request,
                            HttpResponse.BodyHandlers.ofString());

            String body = response.body();

            System.out.println(body);   

            String key = "\"translatedText\":\"";

            int start = body.indexOf(key);

            if(start==-1){

                outputArea.setText("Translation Failed.");

                return;

            }

            start += key.length();

            int end = body.indexOf("\"",start);

            String translated =
                    body.substring(start,end);

            translated = translated.replace("\\u003ci\\u003e","");
            translated = translated.replace("\\u003c/i\\u003e","");

            outputArea.setText(translated);

        }

        catch(IOException | InterruptedException ex){

            JOptionPane.showMessageDialog(this,
                    ex.getMessage());

        }

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LanguageTranslationTool());

    }

}