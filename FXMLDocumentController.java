/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slide_pics;

import static java.awt.Color.blue;
import static java.awt.Color.green;
import static java.awt.Color.red;
import static java.awt.SystemColor.window;
import java.awt.event.MouseEvent;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.FadeTransitionBuilder;
import javafx.animation.FillTransition;
import javafx.animation.FillTransitionBuilder;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
import javafx.animation.PathTransitionBuilder;
import javafx.animation.RotateTransition;
import javafx.animation.RotateTransitionBuilder;
import javafx.animation.StrokeTransition;
import javafx.animation.StrokeTransitionBuilder;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.animation.TranslateTransitionBuilder;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.DisplacementMap;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.FloatMap;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.effect.ImageInput;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.effect.MotionBlur;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.effect.Reflection;
import javafx.scene.effect.SepiaTone;
import javafx.scene.effect.Shadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Path;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author LPR
 */
public class FXMLDocumentController implements Initializable {

//initialisation variables :    
    public int count = 0;
    public int image_index = 0;
    public boolean transitor_next_last = false;
    public Timer timer = new Timer();

    @FXML
    public Label label;
    @FXML
    public ImageView img_v, img_logo;
    @FXML
    public ChoiceBox choiceBox, choiceBox2, choiceBox_transition;
    @FXML
    public ComboBox comboBox;
    @FXML
    public HBox hbox;
    @FXML
    public ImageView img_slide;
    @FXML
    public Slider opacity, sepia, scailling, time_between_pics;
    @FXML
    public Label opacityValue, sepiaValue, scaillingValue, time;
    @FXML
    public Button bouton_importer;
    @FXML
    public TextField largeur, hauteur, echellX, echellY;

    //Partie variable traitement d'image
    double plus_tense_bright = 0.1;
    double moins_tense_bright = -0.1;
    double plus_tense_contrast = 0.1;
    double moins_tense_contrast = -0.1;
    double plus_tense_saturation = 0.1;
    double moins_tense_saturation = -0.1;
    int zoom_plus_value = 1;
    int zoom_moins_value = 1;
    ColorAdjust colorAdjust_bright_moins = new ColorAdjust();
    ColorAdjust colorAdjust_bright_plus = new ColorAdjust();
    ColorAdjust Contrast_plus = new ColorAdjust();
    ColorAdjust Contrast_moins = new ColorAdjust();
    ColorAdjust Saturation_plus = new ColorAdjust();
    ColorAdjust Saturation_moins = new ColorAdjust();

    static Image image_last;

    public final List<String> fileOrder = new ArrayList<String>();
    public final Map<ImageView, String> fileIndex = new HashMap<>();

    //initialisation
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        //trigger effects
        int imageWidth = 0;
        int imageHeight = 0;

        //Blend effect
        Blend blend = new Blend();
        blend.setMode(BlendMode.COLOR_BURN);
        ColorInput blendColorInput = new ColorInput();
        blendColorInput.setPaint(Color.STEELBLUE);
        blendColorInput.setX(0);
        blendColorInput.setY(0);
        blendColorInput.setWidth(imageWidth);
        blendColorInput.setHeight(imageHeight);
        blend.setTopInput(blendColorInput);

        //Bloom effect
        Bloom bloom = new Bloom(0.1);

        //BoxBlur effect
        BoxBlur boxBlur = new BoxBlur();
        boxBlur.setWidth(300);
        boxBlur.setHeight(300);
        boxBlur.setIterations(10);

        //ColorAdjust effect
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setContrast(0.1);
        colorAdjust.setHue(-0.05);
        colorAdjust.setBrightness(0.1);
        colorAdjust.setSaturation(0.2);

        //ColorInput effect
        ColorInput colorInput;
        colorInput = new ColorInput(0, 0,
                imageWidth, imageHeight, Color.STEELBLUE);

        //DisplacementMap effect
        FloatMap floatMap = new FloatMap();
        floatMap.setWidth(imageWidth);
        floatMap.setHeight(imageHeight);

        for (int i = 0; i < imageWidth; i++) {
            double v = (Math.sin(i / 20.0 * Math.PI) - 0.5) / 40.0;
            for (int j = 0; j < imageHeight; j++) {
                floatMap.setSamples(i, j, 0.0f, (float) v);
            }
        }
        DisplacementMap displacementMap = new DisplacementMap();
        displacementMap.setMapData(floatMap);

        //DropShadow effect
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(10.0);
        dropShadow.setOffsetY(5.0);
        dropShadow.setColor(Color.GREY);

        //GaussianBlur effect
        GaussianBlur gaussianBlur = new GaussianBlur();

        //Glow effect
        Glow glow = new Glow(1.0);

        //ImageInput effect
        ImageInput imageInput = new ImageInput(img_v.getImage());

        //InnerShadow effect
        InnerShadow innerShadow = new InnerShadow(5.0, 5.0, 5.0, Color.AZURE);

        //Lighting effect
        Light.Distant light = new Light.Distant();
        light.setAzimuth(50.0);
        light.setElevation(30.0);
        light.setColor(Color.YELLOW);

        Lighting lighting = new Lighting();
        lighting.setLight(light);
        lighting.setSurfaceScale(50.0);

        //MotionBlur effect
        MotionBlur motionBlur = new MotionBlur();
        motionBlur.setRadius(30);
        motionBlur.setAngle(-15.0);

        //PerspectiveTransform effect
        PerspectiveTransform perspectiveTrasform = new PerspectiveTransform();
        perspectiveTrasform.setUlx(0.0);
        perspectiveTrasform.setUly(0.0);
        perspectiveTrasform.setUrx(imageWidth * 1.5);
        perspectiveTrasform.setUry(0.0);
        perspectiveTrasform.setLrx(imageWidth * 3);
        perspectiveTrasform.setLry(imageHeight * 2);
        perspectiveTrasform.setLlx(0);
        perspectiveTrasform.setLly(imageHeight);

        //Reflection effect
        Reflection reflection = new Reflection();
        reflection.setFraction(0.7);

        //SepiaTone effect
        SepiaTone sepiaTone = new SepiaTone();

        //Shadow effect
        Shadow shadow = new Shadow(BlurType.THREE_PASS_BOX, Color.BLUE, 10.0);

        Effect effects[] = {
            null,
            blend,
            bloom,
            boxBlur,
            colorAdjust,
            colorInput,
            displacementMap,
            dropShadow,
            gaussianBlur,
            glow,
            imageInput,
            innerShadow,
            lighting,
            motionBlur,
            perspectiveTrasform,
            reflection,
            sepiaTone,
            shadow
        };

        //initialisation des filetres : 
        ObservableList<String> choiceBoxlist = FXCollections.observableArrayList("rien", "Blend", "Bloom", "BoxBlur", "ColorAdjust",
                "ColorInput", "DisplacementMap", "DropShadow",
                "GaussianBlur", "Glow", "ImageInput", "InnerShadow",
                "Lighting", "MotionBlur", "PerspectiveTransform",
                "Reflection", "SepiaTone", "Shadow");

        choiceBox.setValue("RIEN");
        choiceBox.setItems(choiceBoxlist);
        choiceBox.getSelectionModel().selectFirst();

        choiceBox.getSelectionModel().selectedIndexProperty()
                .addListener((ObservableValue<? extends Number> observable,
                                Number oldValue, Number newValue) -> {
                    img_v.setEffect(effects[newValue.intValue()]);
                });

        ObservableList<String> choiceBoxlist2 = FXCollections.observableArrayList("rien", "obscur", "floue", "inverser");

        choiceBox2.setValue("RIEN");
        choiceBox2.setItems(choiceBoxlist2);
        choiceBox2.getSelectionModel().selectFirst();

        choiceBox2.getSelectionModel().selectedIndexProperty()
                .addListener((ObservableValue<? extends Number> observable,
                                Number oldValue, Number newValue) -> {
                    System.out.print(newValue.intValue());
                    filters(newValue.intValue());
                });

        comboBox.getItems().addAll("rien", "Translate", "Fade", "Rotate");

        //sliders control
        opacity.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                img_v.setOpacity(new_val.doubleValue());
                opacityValue.setText(String.format("%.2f", new_val));
            }
        });

        sepia.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                final SepiaTone sepiaEffect = new SepiaTone();
                img_v.setEffect(sepiaEffect);
                sepiaEffect.setLevel(new_val.doubleValue());
                sepiaValue.setText(String.format("%.2f", new_val));
            }

        });

        time_between_pics.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                time.setText(String.format("%.2f", new_val));
            }

        });

        time_between_pics.setValue(1);

    }

    /*
     //Scale transition
     public static ;
     public static FadeTransition fadeTransition2;
     public void startScale()
     {
     ScaleTransition scaleTransition = ScaleTransitionBuilder.create()
     .node(mainImage)
     .duration(Duration.seconds(4))
     .toX(2)
     .toY(2)
     .cycleCount(Timeline.INDEFINITE)
     .autoReverse(true)
     .build();
     scaleTransition.play();
     }
     */
    //Fonction ajout d'une seule image   
    @FXML
    private void ajouter_image(ActionEvent event) {

        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Ouvrir un repertoire d'images");
        File file = filechooser.showOpenDialog(null);
        if (file != null) {
            img_v.setImage(null);
            String[] extensions = {"jpg", "png", "gif", "bmp"};
            for (String extension : extensions) {
                if (file.getName().toLowerCase().endsWith("." + extension)) {
                    try (FileInputStream stream = new FileInputStream(file);) {
                        final String fileName = file.toURI().toString();
                        fileOrder.add(fileName);

                        Image image = new Image(stream);
                        ImageView imageView = new ImageView(image);

                        imageView.setFitWidth(200);
                        imageView.setFitHeight(200);

                        //img_slid.setFitHeight(image.getHeight()/2);
                        imageView.setStyle("-fx-padding: 10;");
                        imageView.setStyle("-fx-background-color: firebrick;");
                        imageView.setStyle("fx-background-radius: 5;");
                        imageView.setEffect(new DropShadow(20, Color.BLACK));

                        img_v.setImage(image);
                        hbox.getChildren().add(imageView);

                        //Attacher à chaque image un bouton qui donne la possiblité de la supprimer :  
                        Button bouton_supprimer = new Button();
                        bouton_supprimer.setText("X");
                        bouton_supprimer.setStyle("-fx-background-color : red");
                        bouton_supprimer.setPrefHeight(10);
                        bouton_supprimer.setPrefWidth(10);
                        bouton_supprimer.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                int index_img = hbox.getChildren().indexOf(imageView);
                                ImageView imgv = null;

                                if ((index_img + 2) <= hbox.getChildren().size()) {
                                    imgv = (ImageView) hbox.getChildren().get(index_img + 2);
                                    img_v.setImage(imgv.getImage());
                                } else {
                                    img_v.setImage(null);
                                }

                                img_v.setImage(imgv.getImage());
                                hbox.getChildren().remove(imageView);
                                hbox.getChildren().remove(bouton_supprimer);

                            }
                        });
                        hbox.getChildren().add(bouton_supprimer);

                        //affichage de l'image sur l'ImageView principale                      
                        imageView.setOnMouseClicked(e -> {
                            img_v.setImage(image);
                        });

                        fileIndex.put(imageView, fileName);

                        //imageView.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, this);
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);

                    }
                }
            }
        }
    }

    //Sauvegarder de l'imageView
    private void saveImage(ImageView iv) {

        WritableImage image = iv.snapshot(new SnapshotParameters(), null);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        System.out.println(iv.getId());
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image,
                        null), "png", file);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    @FXML
    private void SauvegarderIMG() {
        saveImage(img_v);
    }

    //fonction panaroma
    public void slide_show(int delay) {
        ArrayList<Image> imageArrayList = new ArrayList<>();

        //récupérer les images de hbox
        int i = 0;
        while (i < hbox.getChildren().size()) {

            ImageView image_view = (ImageView) hbox.getChildren().get(i);

            imageArrayList.add(image_view.getImage());
            i = i + 2;
        }

        //Timer timer = new Timer();
        try {
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        img_v.setImage(imageArrayList.get(count++));

                        if (count >= imageArrayList.size()) {
                            count = 0;
                        }
                    });
                }
            }, 1000, delay);
        } catch (Exception e) {
            System.out.println("erreur thread");
        }
    }

    //stoper le panorama
    @FXML
    private void stop_timer() {
        timer.cancel();
        timer = new Timer();
        if (fade == true) {
            stopFade();
        }
        if (Translate == true) {
            stopTranslate();
        }
        if (Rotate == true) {
            stopRotate();
        }

    }

    //démarrer slideshow
    @FXML
    private void Start_Slide_Show() {

        slide_show((int) time_between_pics.getValue() * 1000);

        if (comboBox.getSelectionModel().getSelectedItem().toString() == "Fade") {
            startFade();
        }

        if (comboBox.getSelectionModel().getSelectedItem().toString() == "Translate") {
            startTranslate();
        }

        if (comboBox.getSelectionModel().getSelectedItem().toString() == "Rotate") {
            startRotate();
        }

    }

    //rotation gauche
    boolean switch_on_r = false;
    int rotate_value = 90;

    @FXML
    private void rotate_left() {

        img_v.setRotate(rotate_value);
        rotate_value -= 90;

        if (switch_on_r == false) {
            switch_on_r = true;
        }
    }

    //rotation_droite
    @FXML
    private void rotate_right() {
        if (switch_on_r == true) {
            rotate_value += 180;
        }

        rotate_value += 90;

        switch_on_r = false;
        rotate_value += 90;
        img_v.setRotate(rotate_value);

    }

    //les filtres
    boolean chosed = false;

    //Filters
    public void filters(int filter) {

        if (chosed == false) {
            image_last = img_v.getImage();
        }
        chosed = true;

        if (filter == 0) {
            img_v.setImage(image_last);
        }

        //obscur
        if (filter == 1) {
            Image image = img_v.getImage();
            int width = (int) image.getWidth();
            int height = (int) image.getHeight();
            PixelReader reader = image.getPixelReader();
            WritableImage newImage = new WritableImage(width, height);
            PixelWriter writer = newImage.getPixelWriter();

            for (int i = 0; i < width; i += 1) {
                for (int j = 0; j < height; j += 1) {
                    Color pixel = reader.getColor(i, j);
                    pixel = pixel.darker();
                    writer.setColor(i, j, pixel);
                }
            }
            img_v.setImage(newImage);
        }

        //floue
        if (filter == 2) {

            Image image = img_v.getImage();
            int width = (int) image.getWidth();
            int height = (int) image.getHeight();
            PixelReader reader = image.getPixelReader();
            WritableImage newImage = new WritableImage(width, height);
            PixelWriter writer = newImage.getPixelWriter();

            int step = 1;

            for (int i = 0; i < width; i += 1) {
                for (int j = 0; j < height; j += 1) {
                    int count = 0;
                    double red = 0d, green = 0d, blue = 0d, alpha = 0d;
                    for (int di = -step; di <= step; di += 1) {
                        for (int dj = -step; dj <= step; dj += 1) {
                            final int x = i + di;
                            final int y = j + dj;
                            if (x >= 0 && x < width && y >= 0 && y < height) {
                                count += 1;

                                Color pixel = reader.getColor(x, y);
                                red += pixel.getRed();
                                green += pixel.getGreen();
                                blue += pixel.getBlue();
                                alpha += pixel.getOpacity();

                            }
                        }
                    }
                    Color newColor = new Color(red / count, green / count, blue / count, alpha / count);
                    writer.setColor(i, j, newColor);
                }
            }
            img_v.setImage(newImage);
        }

        if (filter == 3) {
            Image image = img_v.getImage();
            int width = (int) image.getWidth();
            int height = (int) image.getHeight();
            PixelReader reader = image.getPixelReader();
            WritableImage newImage = new WritableImage(width, height);
            PixelWriter writer = newImage.getPixelWriter();

            for (int i = 0; i < width; i += 1) {
                for (int j = 0; j < height; j += 1) {
                    Color pixel = reader.getColor(i, j);
                    pixel = pixel.invert();
                    writer.setColor(i, j, pixel);
                }
            }

            img_v.setImage(newImage);
        }

    }

    //ouvrir plusieurs image
    @FXML
    private void ouvrir_plusieurs_image(ActionEvent event) {

        // import files
        int index_children = 0;
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("choisir un fichier d'images");
        final File folder = chooser.showDialog(null);
        String[] extensions = {"jpg", "png", "gif", "bmp"};
        if (folder != null) {
            final File[] children = folder.listFiles();
            for (File child : children) {
                for (String extension : extensions) {
                    if (child.getName().endsWith("." + extension)) {
                        try (final FileInputStream stream = new FileInputStream(child)) {
                            Image image = new Image(stream);
                            ImageView img_slid = new ImageView(image);

                            img_slid.setFitWidth(200);
                            img_slid.setFitHeight(200);

                            //img_slid.setFitHeight(image.getHeight()/2);
                            img_slid.setStyle("-fx-padding: 10;");
                            img_slid.setStyle("-fx-background-color: firebrick;");
                            img_slid.setStyle("fx-background-radius: 5;");
                            img_slid.setEffect(new DropShadow(20, Color.BLACK));

                            hbox.getChildren().add(img_slid);
                            if (index_children == 0) {
                                img_v.setImage(image);
                            }
                            index_children++;

                                               //when image is clicked 
                            img_slid.setOnMouseClicked(e -> {
                                img_v.setImage(image);
                            });

                            Button btn = new Button();
                            btn.setText("X");
                            btn.setStyle("-fx-background-color : red");
                            btn.setPrefHeight(10);
                            btn.setPrefWidth(10);
                            btn.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    int index_img = hbox.getChildren().indexOf(img_slid);
                                    ImageView imgv = null;

                                    if ((index_img + 2) <= hbox.getChildren().size()) {
                                        imgv = (ImageView) hbox.getChildren().get(index_img + 2);
                                        img_v.setImage(imgv.getImage());
                                    } else {
                                        img_v.setImage(null);
                                    }

                                    hbox.getChildren().remove(img_slid);
                                    hbox.getChildren().remove(btn);
                                }
                            });
                            hbox.getChildren().add(btn);

                            hbox.setSpacing(5);

                            effect_image_mouse(image, img_slid);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        } else {
            javax.swing.JOptionPane.showMessageDialog(null, "Le fichier est vide");
        }

    }

    //assombrir l'image quand le curseurs pas dessus
    private void effect_image_mouse(Image image, ImageView img_v) {

        img_v.setImage(image);

        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.5);

        img_v.setOnMouseEntered(e -> img_v.setEffect(colorAdjust));
        img_v.setOnMouseExited(e -> img_v.setEffect(null));

    }

    //Fonction zoom plus
    int zoom_in = 400;
    boolean switch_on = false;

    @FXML
    private void zoom_plus() {
        final DoubleProperty zoomProperty = new SimpleDoubleProperty(zoom_in);
        img_v.setFitWidth(zoomProperty.get());
        img_v.setFitHeight(zoomProperty.get());

        zoom_in += 50;

        if (switch_on == false) {
            switch_on = true;
        }
        //zoom_plus_value++;
    }

    //fonction zoom_moins
    @FXML
    private void zoom_moins() {
        if (switch_on == true) {
            zoom_in -= 100;
        }
        zoom_in -= 50;
        final DoubleProperty zoomProperty = new SimpleDoubleProperty(zoom_in);
        img_v.setFitWidth(zoomProperty.get());
        img_v.setFitHeight(zoomProperty.get());
        switch_on = false;

        zoom_moins_value++;
    }

    //afficher la prochaine image
    @FXML
    private void Next_Image() {
        ImageView imagev = (ImageView) hbox.getChildren().get(image_index);
        img_v.setImage(imagev.getImage());
        if (transitor_next_last) {
            image_index += 2;
        }
        image_index += 2;
        transitor_next_last = false;
        if (image_index == hbox.getChildren().size()) {
            image_index = 0;
        }

    }

    //afficher la dernière image
    @FXML
    private void Last_Image() {

        if (image_index <= -2) {
            image_index = hbox.getChildren().size();
        }

        if (transitor_next_last == false) {
            image_index -= 2;
        }
        image_index -= 2;
        transitor_next_last = true;
        ImageView imagev = (ImageView) hbox.getChildren().get(image_index);
        img_v.setImage(imagev.getImage());

    }

    //augmenter la luminisoté 
    @FXML
    private void brightness_plus() {

        colorAdjust_bright_plus.setBrightness(plus_tense_bright);
        img_v.setEffect(colorAdjust_bright_plus);
        plus_tense_bright += 0.1;
        moins_tense_bright = plus_tense_bright - 0.1;
    }

    //baisser la luminisoté
    @FXML
    private void brightness_moins() {
        colorAdjust_bright_moins.setBrightness(moins_tense_bright);
        img_v.setEffect(colorAdjust_bright_moins);
        moins_tense_bright -= 0.1;
        plus_tense_bright = moins_tense_bright + 0.1;
    }

    //augmenter le constrat
    @FXML
    private void contrast_plus() {
        Contrast_plus.setContrast(plus_tense_contrast);
        img_v.setEffect(Contrast_plus);
        plus_tense_contrast += 0.1;
        moins_tense_contrast = plus_tense_contrast - 0.1;
    }

    //baisser le constrat
    @FXML
    private void contrast_moins() {
        Contrast_moins.setContrast(moins_tense_contrast);
        img_v.setEffect(Contrast_moins);
        moins_tense_contrast -= 0.1;
        plus_tense_contrast = moins_tense_contrast + 0.1;
    }

    //augmenter la saturation
    @FXML
    private void saturation_plus() {
        Saturation_plus.setSaturation(plus_tense_saturation);
        img_v.setEffect(Saturation_plus);
        plus_tense_saturation += 0.1;
        moins_tense_saturation = plus_tense_saturation - 0.1;
    }

    //baisser la saturation
    @FXML
    private void saturation_moins() {
        Saturation_moins.setSaturation(moins_tense_saturation);
        img_v.setEffect(Saturation_moins);
        moins_tense_saturation -= 0.1;
        plus_tense_saturation = moins_tense_saturation + 0.1;
    }

    //fonction couper l'image
    @FXML
    private void redimensionner() {
        Image img = img_v.getImage();
        if (img != null) {
            int x = Integer.parseInt(echellX.getText());
            int y = Integer.parseInt(echellY.getText());
            int width = Integer.parseInt(largeur.getText());
            int height = Integer.parseInt(hauteur.getText());
            WritableImage croppedImage = new WritableImage(img_v.getImage().getPixelReader(), x, y, width, height);
            img_v.setImage(croppedImage);
        }

    }

    //Transition
    public static FadeTransition fadeTransition;
    public static FadeTransition fadeTransition2;
    boolean fade = false;
   //-----------------transitions

    public void startFade() {
        int time = (int) time_between_pics.getValue();

        fadeTransition = FadeTransitionBuilder.create()
                .duration(Duration.seconds(time))
                .node(img_v)
                .fromValue(1)
                .toValue(0.2)
                .cycleCount(Timeline.INDEFINITE)
                .autoReverse(false)
                .build();
        fadeTransition.play();
        fade = true;
    }

    public void stopFade() {
        fadeTransition.stop();
        fadeTransition2 = FadeTransitionBuilder.create()
                .duration(Duration.seconds(2))
                .node(img_v)
                .fromValue(1)
                .toValue(1)
                .cycleCount(Timeline.INDEFINITE)
                .autoReverse(false)
                .build();
        fadeTransition2.play();
        //fadeTransition.stop();
        img_v.setEffect(null);
        fade = false;

    }

    //transition stroke
    //Transition
    public static TranslateTransition translateTransition;
    //public static StrokeTransition StrokeTransition2;
    boolean Translate = false;

   //-----------------transitions
    public void startTranslate() {
        int time = (int) time_between_pics.getValue();
        //strokeTransition = new StrokeTransition(Duration.millis(3000), , Color.RED, Color.BLUE);
        translateTransition = TranslateTransitionBuilder.create()
                .node(img_v)
                .duration(Duration.seconds(time))
                .byX(400f)
                .cycleCount(3000)
                .autoReverse(true)
                .build();
        translateTransition.play();

        Translate = true;
    }

    public void stopTranslate() {

        translateTransition.stop();
        Translate = false;

    }

    boolean Rotate = false;

    public static RotateTransition rotateTransition;
    public static RotateTransition rotateTransition2;

    public void startRotate() {
        rotateTransition = RotateTransitionBuilder.create()
                .node(img_v)
                .duration(Duration.seconds(4))
                .fromAngle(0)
                .toAngle(360)
                .cycleCount(Timeline.INDEFINITE)
                .autoReverse(false)
                .build();
        rotateTransition.play();
        Rotate = true;
    }

    public void stopRotate() {
        rotateTransition.stop();
        Rotate = false;
    }

}
