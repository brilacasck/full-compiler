/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asck_compiler;

import asck_lex.RegexSimplfier;
import asck_lex.Tokenizer;
import clr.ParseTable;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 *
 * @author ALIREZA
 */
public class ASCK_COMPILER extends Application {

    private Button btn;
    private Button btn_load;
    private Button btn_load2;
    private Button btn_exit;
    private Button in1;
    private Button in2;

    private Button btn_parse;
    Text t;

    private TextArea input;
    private TextArea output;
    private TextArea errors;
    private Tokenizer tk;
    ArrayList<Pair<String, String>> tkArray;

    private File file;
    private File file2;

    public Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private double width = screenSize.getWidth();
    private double height = screenSize.getHeight();

    @Override
    public void start(Stage primaryStage) {
        file = new File("lexeme2.conf");

        input = new TextArea();
        input.setFocusTraversable(false);
        input.setPromptText("Code Input");
        input.setPrefWidth(10 * width / 12 - 50);
        input.setPrefHeight(5 * height / 12);
        input.setTranslateX(width / 12 + 50);
        input.setTranslateY(10);
        input.setStyle("-fx-background-color: #44f;-fx-font-size:23;"
                + "-fx-border-insets: 2;\n"
                + "-fx-border-width: 1;\n"
                + "-fx-border-style: solid inside;\n"
                + "-fx-border-style: segments(10, 15, 15, 15)  line-cap round ;");

        btn = new Button();
        btn.setText("Tokenize");
        btn.setTranslateX(15);
        btn.setTranslateY(20);
        btn.setMinHeight(50);
        btn.setStyle("-fx-color: #459;-fx-font-size:21;");
        btn.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                btn.setStyle(btn.getStyle()
                        + "-fx-background-color: -fx-shadow-highlight-color, -fx-outer-border, -fx-inner-border, -fx-body-color;"
                        + " -fx-text-fill: black;");
            }
        });
        btn.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                btn.setStyle(btn.getStyle().replace("-fx-background-color: -fx-shadow-highlight-color, -fx-outer-border, -fx-inner-border, -fx-body-color; -fx-text-fill: black;", ""));
            }
        });

        btn_load = new Button();
        btn_load.setText("Load Lexeme");
        btn_load.setTranslateX(15);
        btn_load.setTranslateY(20 + 10 + btn.getMinHeight());
        btn_load.setMinHeight(50);
        btn_load.setStyle("-fx-color: #954;-fx-font-size:21;");
        btn_load.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                btn_load.setStyle(btn_load.getStyle()
                        + "-fx-background-color: -fx-shadow-highlight-color, -fx-outer-border, -fx-inner-border, -fx-body-color;"
                        + " -fx-text-fill: black;");
            }
        });
        btn_load.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                btn_load.setStyle(btn_load.getStyle().replace("-fx-background-color: -fx-shadow-highlight-color, -fx-outer-border, -fx-inner-border, -fx-body-color; -fx-text-fill: black;", ""));
            }
        });

        btn_load2 = new Button();
        btn_load2.setText("Load Grammar");
        btn_load2.setTranslateX(15);
        btn_load2.setTranslateY(20*6 + 4*10 + btn.getMinHeight());
        btn_load2.setMinHeight(50);
        btn_load2.setStyle("-fx-color: #954;-fx-font-size:21;");
        btn_load2.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                btn_load2.setStyle(btn_load2.getStyle()
                        + "-fx-background-color: -fx-shadow-highlight-color, -fx-outer-border, -fx-inner-border, -fx-body-color;"
                        + " -fx-text-fill: black;");
            }
        });
        btn_load2.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                btn_load2.setStyle(btn_load2.getStyle().replace("-fx-background-color: -fx-shadow-highlight-color, -fx-outer-border, -fx-inner-border, -fx-body-color; -fx-text-fill: black;", ""));
            }
        });

        btn_parse = new Button();
        btn_parse.setText("Parse");
        btn_parse.setTranslateX(15);
        btn_parse.setTranslateY(20 * 4 + 10 * 2 + btn.getMinHeight());
        btn_parse.setMinHeight(50);
        btn_parse.setStyle("-fx-color: #954;-fx-font-size:21;");
        btn_parse.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                btn_parse.setStyle(btn_parse.getStyle()
                        + "-fx-background-color: -fx-shadow-highlight-color, -fx-outer-border, -fx-inner-border, -fx-body-color;"
                        + " -fx-text-fill: black;");
            }
        });
        btn_parse.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                btn_parse.setStyle(btn_parse.getStyle().replace("-fx-background-color: -fx-shadow-highlight-color, -fx-outer-border, -fx-inner-border, -fx-body-color; -fx-text-fill: black;", ""));
            }
        });

        btn_exit = new Button();
        btn_exit.setText("✘");
        btn_exit.setTranslateX(width - 45);
        btn_exit.setTranslateY(10);
        btn_exit.setMinHeight(10);
        btn_exit.setMinWidth(10);
        btn_exit.setScaleX(1.1);
        btn_exit.setScaleY(1.1);
        btn_exit.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                btn_exit.setScaleX(1);
                btn_exit.setScaleY(1);
                btn_exit.setStyle(btn_exit.getStyle().replaceAll("-fx-background-radius:.+;", "-fx-background-radius: 25;"));
            }
        });
        btn_exit.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                btn_exit.setScaleX(1.1);
                btn_exit.setScaleY(1.1);
                btn_exit.setStyle(btn_exit.getStyle().replaceAll("-fx-background-radius:.+;", "-fx-background-radius: 0;"));
            }
        });
        btn_exit.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                primaryStage.hide();
            }
        });
        btn_exit.setStyle("-fx-color: #954;-fx-font-size:21;");

        in1 = new Button();
        in1.setText("input 1");
        in1.setTranslateX(width - 120);
        in1.setTranslateY(2 * height / 12);
        in1.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                File f = new File(".\\input1.in");
                if (f.isFile()) {
                    try {
                        FileReader fr = new FileReader(f);
                        BufferedReader br = new BufferedReader(fr);
                        input.setText("");
                        br.lines().forEach((str) -> {
                            input.setText(input.getText() + str + "\n");
                        });
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(ASCK_COMPILER.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        });

        in2 = new Button();
        in2.setText("input 2");
        in2.setTranslateX(width - 120);
        in2.setTranslateY(2 * height / 12 + 50);
        in2.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                File f = new File(".\\input2.in");
                if (f.isFile()) {
                    try {
                        FileReader fr = new FileReader(f);
                        BufferedReader br = new BufferedReader(fr);
                        input.setText("");
                        br.lines().forEach((str) -> {
                            input.setText(input.getText() + str + "\n");
                        });
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(ASCK_COMPILER.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        });

        output = new TextArea();
        output.setFocusTraversable(false);
        output.setPromptText("Tokenized Output");
        output.setPrefWidth(5.5 * width / 12);
        output.setPrefHeight(5 * height / 12);
        output.setTranslateX(10);
        output.setTranslateY(7 * height / 12 - 40);
        output.setWrapText(true);
        output.setEditable(false);
        output.setStyle("-fx-background-color: #4f4;-fx-font-size:23;"
                + "-fx-border-insets: 2;\n"
                + "-fx-border-width: 1;\n"
                + "-fx-border-style: solid;\n"
                + "-fx-border-style: segments(10, 15, 15, 15)  line-cap round ;");

        errors = new TextArea();
        errors.setFocusTraversable(false);
        errors.setPromptText("Errors");
        errors.setPrefWidth(5.5 * width / 12);
        errors.setPrefHeight(5 * height / 12);
        errors.setTranslateX(6.5 * width / 12 - 10);
        errors.setTranslateY(7 * height / 12 - 40);
        errors.setWrapText(true);
        errors.setEditable(false);
        errors.setStyle("-fx-background-color: #f44;-fx-font-size:23;"
                + "-fx-border-insets: 2;\n"
                + "-fx-border-width: 1;\n"
                + "-fx-border-style: solid;\n"
                + "-fx-border-style: segments(10, 15, 15, 15)  line-cap round ;");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Config File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Lexeme Configuration File", "*.conf"));
        fileChooser.setInitialDirectory(new File("."));

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    RegexSimplfier rs = new RegexSimplfier(file);
                    String[] s = rs.Simplify();
                    //showNewRegex(s);
                    String[] lefts = new String[s.length];
                    String[] rights = new String[s.length];
                    for (int i = 0; i < s.length; i++) {
                        String str = s[i];
                        String parts[] = str.split(":=");
                        lefts[i] = parts[0].trim();
                        rights[i] = parts[1].trim();
                    }
                    output.setText("");
                    tk = new Tokenizer(input.getText(), 20, lefts, rights, rs.getPrimaryRegex());
                    tkArray = tk.tokenize();
                    String tmp = "";
                    for (int i = 0; i < tkArray.size(); i++) {
                        tmp += "<" + tkArray.get(i).getKey() + "," + tkArray.get(i).getValue() + "> ";
                    }
                    output.setText(tmp);

                    String errorStr = "";
                    int count = -1;
                    for (int i = 0; i < tk.getErrors()[0].size(); i++) {
                        boolean flag = false;
                        if (Integer.parseInt(tk.getErrors()[0].get(i)) != count) {
                            if (count != -1) {
                                errorStr += "  }\n";
                            }
                            errorStr += "Line " + ((Integer.parseInt(tk.getErrors()[0].get(i)) + 1)) + ":\t{  ";
                            count++;
                            flag = true;
                        }
                        if (!flag) {
                            errorStr += "  ,  ";
                        }
                        errorStr += tk.getErrors()[1].get(i);
                    }
                    if (!errorStr.isEmpty()) {
                        errorStr += "  }";
                    }
                    errors.setText(errorStr);

                } catch (Exception ex) {
                    Logger.getLogger(ASCK_COMPILER.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        btn_load.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                file = fileChooser.showOpenDialog(primaryStage);
            }
        });

        btn_load2.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                file2 = fileChooser.showOpenDialog(primaryStage);
            }
        });

        t = new Text();
        t.setText("");
        t.setTranslateX(width / 2 - 100);
        t.setTranslateY(height / 2 - 40);
        t.setScaleX(4);
        t.setScaleY(4);
        file2 = new File("grammar1.conf");
        btn_parse.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                ParseTable ps = new ParseTable(file2.getPath());
                ArrayList<String> inputs = new ArrayList<>();

                for (int i = 0; i < tkArray.size(); i++) {
                    if ("ID".equals(tkArray.get(i).getKey()) || "LITERAL".equals(tkArray.get(i).getKey())) {
                        inputs.add(tkArray.get(i).getKey().toLowerCase());
                    } else {
                        inputs.add(tkArray.get(i).getValue());
                    }
                    System.out.println("***" + inputs.get(inputs.size() - 1));
                }
                inputs.add("$");
                boolean b;
                System.out.println(b = ps.Analyse(inputs));
                if (b) {
                    t.setText("OH YEAH, YOU ARE BITCHIN'");
                    t.setStroke(Color.GREENYELLOW);
                } else {
                    t.setText("OH NO, YOU HAVE SCREWED UP");
                    t.setStroke(Color.RED);
                }
            }
        });

        Group root = new Group();
        root.getChildren().add(input);
        root.getChildren().add(btn);
        root.getChildren().add(btn_load);
        root.getChildren().add(btn_exit);
        root.getChildren().add(in1);
        root.getChildren().add(in2);
        root.getChildren().add(output);
        root.getChildren().add(errors);
        root.getChildren().add(btn_parse);
        root.getChildren().add(btn_load2);
        root.getChildren().add(t);

        input.setStyle(input.getStyle() + "-fx-text-fill: #03A9F4;");
        output.setStyle(output.getStyle() + "-fx-text-fill: #66BB6A;");
        errors.setStyle(errors.getStyle() + "-fx-text-fill: #ef5350;");
        btn.setStyle("    -fx-background-color: \n"
                + "        linear-gradient(#686868 0%, #232723 25%, #373837 75%, #757575 100%),\n"
                + "        linear-gradient(#020b02, #3a3a3a),\n"
                + "        linear-gradient(#9d9e9d 0%, #6b6a6b 20%, #343534 80%, #242424 100%),\n"
                + "        linear-gradient(#8a8a8a 0%, #6b6a6b 20%, #343534 80%, #262626 100%),\n"
                + "        linear-gradient(#777777 0%, #606060 50%, #505250 51%, #2a2b2a 100%);\n"
                + "    -fx-background-insets: 0,1,4,5,6;\n"
                + "    -fx-background-radius: 9,8,5,4,3;\n"
                + "    -fx-padding: 15 30 15 30;\n"
                + "    -fx-font-family: \"Helvetica\";\n"
                + "    -fx-font-size: 18px;\n"
                + "    -fx-font-weight: bold;\n"
                + "    -fx-text-fill: white;\n"
                + "    -fx-effect: dropshadow( three-pass-box , rgba(255,255,255,0.2) , 1, 0.0 , 0 , 1);");
        btn_parse.setStyle("    -fx-background-color: \n"
                + "        linear-gradient(#686868 0%, #232723 25%, #373837 75%, #757575 100%),\n"
                + "        linear-gradient(#020b02, #3a3a3a),\n"
                + "        linear-gradient(#9d9e9d 0%, #6b6a6b 20%, #343534 80%, #242424 100%),\n"
                + "        linear-gradient(#8a8a8a 0%, #6b6a6b 20%, #343534 80%, #262626 100%),\n"
                + "        linear-gradient(#777777 0%, #606060 50%, #505250 51%, #2a2b2a 100%);\n"
                + "    -fx-background-insets: 0,1,4,5,6;\n"
                + "    -fx-background-radius: 9,8,5,4,3;\n"
                + "    -fx-padding: 15 30 15 30;\n"
                + "    -fx-font-family: \"Helvetica\";\n"
                + "    -fx-font-size: 18px;\n"
                + "    -fx-font-weight: bold;\n"
                + "    -fx-text-fill: white;\n"
                + "    -fx-effect: dropshadow( three-pass-box , rgba(255,255,255,0.2) , 1, 0.0 , 0 , 1);");
        btn_parse.setStyle("    -fx-background-color: \n"
                + "        linear-gradient(#686868 0%, #232723 25%, #373837 75%, #757575 100%),\n"
                + "        linear-gradient(#020b02, #3a3a3a),\n"
                + "        linear-gradient(#9d9e9d 0%, #6b6a6b 20%, #343534 80%, #242424 100%),\n"
                + "        linear-gradient(#8a8a8a 0%, #6b6a6b 20%, #343534 80%, #262626 100%),\n"
                + "        linear-gradient(#777777 0%, #606060 50%, #505250 51%, #2a2b2a 100%);\n"
                + "    -fx-background-insets: 0,1,4,5,6;\n"
                + "    -fx-background-radius: 9,8,5,4,3;\n"
                + "    -fx-padding: 15 30 15 30;\n"
                + "    -fx-font-family: \"Helvetica\";\n"
                + "    -fx-font-size: 18px;\n"
                + "    -fx-font-weight: bold;\n"
                + "    -fx-text-fill: white;\n"
                + "    -fx-effect: dropshadow( three-pass-box , rgba(255,255,255,0.2) , 1, 0.0 , 0 , 1);");
        
        btn_load.setStyle("    -fx-background-color: \n"
                + "        linear-gradient(#686868 0%, #232723 25%, #373837 75%, #757575 100%),\n"
                + "        linear-gradient(#020b02, #3a3a3a),\n"
                + "        linear-gradient(#9d9e9d 0%, #6b6a6b 20%, #343534 80%, #242424 100%),\n"
                + "        linear-gradient(#8a8a8a 0%, #6b6a6b 20%, #343534 80%, #262626 100%),\n"
                + "        linear-gradient(#777777 0%, #606060 50%, #505250 51%, #2a2b2a 100%);\n"
                + "    -fx-background-insets: 0,1,4,5,6;\n"
                + "    -fx-background-radius: 9,8,5,4,3;\n"
                + "    -fx-padding: 15 30 15 30;\n"
                + "    -fx-font-family: \"Helvetica\";\n"
                + "    -fx-font-size: 18px;\n"
                + "    -fx-font-weight: bold;\n"
                + "    -fx-text-fill: white;\n"
                + "    -fx-effect: dropshadow( three-pass-box , rgba(255,255,255,0.2) , 1, 0.0 , 0 , 1);");
        btn_load.setStyle("    -fx-background-color: \n"
                + "        linear-gradient(#686868 0%, #232723 25%, #373837 75%, #757575 100%),\n"
                + "        linear-gradient(#020b02, #3a3a3a),\n"
                + "        linear-gradient(#9d9e9d 0%, #6b6a6b 20%, #343534 80%, #242424 100%),\n"
                + "        linear-gradient(#8a8a8a 0%, #6b6a6b 20%, #343534 80%, #262626 100%),\n"
                + "        linear-gradient(#777777 0%, #606060 50%, #505250 51%, #2a2b2a 100%);\n"
                + "    -fx-background-insets: 0,1,4,5,6;\n"
                + "    -fx-background-radius: 9,8,5,4,3;\n"
                + "    -fx-padding: 15 30 15 30;\n"
                + "    -fx-font-family: \"Helvetica\";\n"
                + "    -fx-font-size: 18px;\n"
                + "    -fx-font-weight: bold;\n"
                + "    -fx-text-fill: white;\n"
                + "    -fx-effect: dropshadow( three-pass-box , rgba(255,255,255,0.2) , 1, 0.0 , 0 , 1);");

        btn_load2.setStyle("    -fx-background-color: \n"
                + "        linear-gradient(#686868 0%, #232723 25%, #373837 75%, #757575 100%),\n"
                + "        linear-gradient(#020b02, #3a3a3a),\n"
                + "        linear-gradient(#9d9e9d 0%, #6b6a6b 20%, #343534 80%, #242424 100%),\n"
                + "        linear-gradient(#8a8a8a 0%, #6b6a6b 20%, #343534 80%, #262626 100%),\n"
                + "        linear-gradient(#777777 0%, #606060 50%, #505250 51%, #2a2b2a 100%);\n"
                + "    -fx-background-insets: 0,1,4,5,6;\n"
                + "    -fx-background-radius: 9,8,5,4,3;\n"
                + "    -fx-padding: 15 30 15 30;\n"
                + "    -fx-font-family: \"Helvetica\";\n"
                + "    -fx-font-size: 18px;\n"
                + "    -fx-font-weight: bold;\n"
                + "    -fx-text-fill: white;\n"
                + "    -fx-effect: dropshadow( three-pass-box , rgba(255,255,255,0.2) , 1, 0.0 , 0 , 1);");
        btn_load2.setStyle("    -fx-background-color: \n"
                + "        linear-gradient(#686868 0%, #232723 25%, #373837 75%, #757575 100%),\n"
                + "        linear-gradient(#020b02, #3a3a3a),\n"
                + "        linear-gradient(#9d9e9d 0%, #6b6a6b 20%, #343534 80%, #242424 100%),\n"
                + "        linear-gradient(#8a8a8a 0%, #6b6a6b 20%, #343534 80%, #262626 100%),\n"
                + "        linear-gradient(#777777 0%, #606060 50%, #505250 51%, #2a2b2a 100%);\n"
                + "    -fx-background-insets: 0,1,4,5,6;\n"
                + "    -fx-background-radius: 9,8,5,4,3;\n"
                + "    -fx-padding: 15 30 15 30;\n"
                + "    -fx-font-family: \"Helvetica\";\n"
                + "    -fx-font-size: 18px;\n"
                + "    -fx-font-weight: bold;\n"
                + "    -fx-text-fill: white;\n"
                + "    -fx-effect: dropshadow( three-pass-box , rgba(255,255,255,0.2) , 1, 0.0 , 0 , 1);");

        btn_exit.setStyle("-fx-background-color: linear-gradient(#ff5400, #be1d00);\n"
                + "    -fx-background-radius: 0;\n"
                + "    -fx-background-insets: 0;\n"
                + "    -fx-text-fill: white;");
        Scene scene = new Scene(root, 950, 800, Color.web("263238", .9));
        Image icon = new Image(getClass().getResourceAsStream("ASCK.PNG"));
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("ASCK LEX");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();

        //
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
