/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilteringfx;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * @author Mohammad Hossein Rimaz
 */
public class ImageFilteringFX extends Application {

    private Image src;
    private Image dest;

    private final double factor = 1;
    private final double offset = 1;

    private int width;
    private int height;

    private RadioButton identityButton;
    private RadioButton blurButton;
    private RadioButton gausianBlurButton;
    private RadioButton sharpenButton;
    private RadioButton embossButton;
    private RadioButton topSobleButton;

    static final short BITS = 256;

    private final double[][] IDENTITY = new double[][]{
        {0, 0, 0},
        {0, 1, 0},
        {0, 0, 0}};
    private final double[][] BLUR = new double[][]{
        {1, 1, 1},
        {1, 1, 1},
        {1, 1, 1}};
    private final double[][] GAUSIAN_BLUR = new double[][]{
        {1, 4, 6, 4, 1},
        {4, 16, 24, 16, 4},
        {6, 24, 36, 24, 6},
        {4, 16, 24, 16, 4},
        {1, 4, 6, 4, 1}};
    private final double[][] SHARPEN = new double[][]{
        {0, -1, 0},
        {-1, 5, -1},
        {0, -1, 0}};
    private final double[][] EMBOSS = new double[][]{
        {-2, -1, 0},
        {-1, 1, 1},
        {0, 1, 2}};
    private final double[][] TOP_SOBEL = new double[][]{
        {1, 2, 1},
        {0, 0, 0},
        {-1, -2, -1}};

    @Override
    public void start(Stage stage) {

        AnchorPane root = new AnchorPane();

        initImage(root);

        Scene scene = new Scene(root);

        stage.setTitle("Image Processing Demo");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void initImage(AnchorPane root) {
        src = new Image(getClass().getResourceAsStream("duke_badge.png"));

        ImageView srcView = new ImageView(src);
        root.getChildren().add(srcView);
        AnchorPane.setTopAnchor(srcView, 0.0);
        AnchorPane.setLeftAnchor(srcView, 0.0);

        width = (int) src.getWidth();
        height = (int) src.getHeight();
        root.setPrefSize(width * 2.0, height + 50);

        dest = src;
        ImageView destView = new ImageView(dest);
        destView.setTranslateX(width);
        root.getChildren().add(destView);
        AnchorPane.setTopAnchor(destView, 0.0);
        AnchorPane.setRightAnchor(destView, (double) width);

        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER);
        hbox.setPrefWidth(width);
        hbox.setPrefHeight(50);
        root.getChildren().add(hbox);
        AnchorPane.setBottomAnchor(hbox, 0.0);
        AnchorPane.setLeftAnchor(hbox, 10.0);
        AnchorPane.setRightAnchor(hbox, 10.0);

        ToggleGroup group = new ToggleGroup();
        identityButton = new RadioButton("Identity");
        identityButton.setToggleGroup(group);
        identityButton.setSelected(true);
        hbox.getChildren().add(identityButton);
        blurButton = new RadioButton("Box Blur");
        blurButton.setToggleGroup(group);
        hbox.getChildren().add(blurButton);
        gausianBlurButton = new RadioButton("Gausian Blur");
        gausianBlurButton.setToggleGroup(group);
        hbox.getChildren().add(gausianBlurButton);
        embossButton = new RadioButton("Emboss");
        embossButton.setToggleGroup(group);
        hbox.getChildren().add(embossButton);
        sharpenButton = new RadioButton("Sharpen");
        sharpenButton.setToggleGroup(group);
        hbox.getChildren().add(sharpenButton);
        topSobleButton = new RadioButton("Top Sobel");
        topSobleButton.setToggleGroup(group);
        hbox.getChildren().add(topSobleButton);

        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            RadioButton button = (RadioButton) newValue;
            if (button.equals(topSobleButton)) {
                destView.setImage(dest = apply(src, TOP_SOBEL, 1, 0));
            } else if (button.equals(sharpenButton)) {
                destView.setImage(dest = apply(src, SHARPEN, 1, 0));
            } else if (button.equals(embossButton)) {
                destView.setImage(dest = apply(src, EMBOSS, 1, 0));
            } else if (button.equals(blurButton)) {
                destView.setImage(dest = apply(src, BLUR, 9, 0));
            } else if (button.equals(gausianBlurButton)) {
                destView.setImage(dest = apply(src, GAUSIAN_BLUR, 256, 0));
            } else if (button.equals(identityButton)) {
                destView.setImage(dest = apply(src, IDENTITY, 1, 0));
            }
        });

        Button applyButton = new Button("Apply Again");
        hbox.getChildren().add(applyButton);
        applyButton.setOnAction((event) -> {
            RadioButton button = (RadioButton) group.getSelectedToggle();
            if (button.equals(topSobleButton)) {
                destView.setImage(dest = apply(dest, TOP_SOBEL, 1, 0));
            } else if (button.equals(sharpenButton)) {
                destView.setImage(dest = apply(dest, SHARPEN, 1, 0));
            } else if (button.equals(embossButton)) {
                destView.setImage(dest = apply(dest, EMBOSS, 1, 0));
            } else if (button.equals(blurButton)) {
                destView.setImage(dest = apply(dest, BLUR, 9, 0));
            } else if (button.equals(gausianBlurButton)) {
                destView.setImage(dest = apply(dest, GAUSIAN_BLUR, 256, 0));
            } else if (button.equals(identityButton)) {
                destView.setImage(dest = apply(dest, IDENTITY, 1, 0));
            }
        });
    }

    /**
     * this method apply filter which specified by it's kernel matrix to the
     * image using matrix convolution filtering
     *
     * @param input input image
     * @param kernel kernel matrix to filter image
     * @param divisor divisor
     * @param offset offset
     * @return new filtered image
     */
    public Image apply(Image input, double[][] kernel, double divisor, double offset) {
        // Reader for the original image
        PixelReader pixelReader = input.getPixelReader();
        int imageWidth = (int) input.getWidth();
        int imageHeight = (int) input.getHeight();

        // Create new image and get its writer
        WritableImage result = new WritableImage(imageWidth, imageHeight);
        PixelWriter pixelWriter = result.getPixelWriter();

        // Apply transformation (without borders)
        int tamMidKernel = (kernel.length - 1) / 2;
        Color[][] pixels = new Color[kernel.length][kernel.length];
        int newR, newG, newB, sumR, sumG, sumB;
        double a;
        Color newColor;
        for (int y = 0; y < input.getHeight() - (kernel.length - 1); y++) {
            for (int x = 0; x < input.getWidth() - (kernel.length - 1); x++) {
                // Get pixel matrix
                for (int i = 0; i < kernel.length; ++i) {
                    for (int j = 0; j < kernel.length; ++j) {
                        pixels[i][j] = pixelReader.getColor(x + i, y + j);
                    }
                }
                // Get sum of RGB multiplied by kernel
                sumR = sumG = sumB = 0;
                for (int i = 0; i < kernel.length; ++i) {
                    for (int j = 0; j < kernel.length; ++j) {
                        // (from [0,1) to [0,BITS)) * kernel
                        sumR += (pixels[i][j].getRed() * (BITS - 1))
                                * kernel[i][j];
                        sumG += (pixels[i][j].getGreen() * (BITS - 1))
                                * kernel[i][j];
                        sumB += (pixels[i][j].getBlue() * (BITS - 1))
                                * kernel[i][j];
                    }
                }
                // Get final RGB
                newR = (int) (sumR / divisor + offset);
                newR = checkInRange(newR);
                newG = (int) (sumG / divisor + offset);
                newG = checkInRange(newG);
                newB = (int) (sumB / divisor + offset);
                newB = checkInRange(newB);
                a = pixels[tamMidKernel + 1][tamMidKernel + 1].getOpacity();
                newColor = Color.rgb(newR, newG, newB, a);
                // Write new pixel
                pixelWriter.setColor(x + tamMidKernel, y + tamMidKernel,
                        newColor);
            }
        }

        // Copy borders from original image
        for (int i = 0; i < input.getWidth(); i++) {
            for (int j = 0; j < tamMidKernel; j++) {
                // Top border
                pixelWriter.setColor(i, j, pixelReader.getColor(i, j));
                // Botton border
                pixelWriter.setColor(i, (int) (input.getHeight() - 1 - j),
                        pixelReader.getColor(i, (int) (input.getHeight() - 1 - j)));
            }
        }
        for (int i = 0; i < input.getHeight(); i++) {
            for (int j = 0; j < tamMidKernel; j++) {
                // Right border
                pixelWriter.setColor((int) (input.getWidth() - 1 - j), i,
                        pixelReader.getColor((int) (input.getWidth() - 1 - j), i));

                // Left border
                pixelWriter.setColor(j, i, pixelReader.getColor(j, i));
            }
        }

        return result;
    }

    /**
     * Check the pixel is in the range [0,BITS).
     *
     * @param pixel
     * @return pixel if in range; 0 if < 0; 255 if > 255
     */
    private int checkInRange(int pixel) {
        if (pixel < 0) {
            return 0;
        } else if (pixel >= BITS) {
            return BITS - 1;
        } else {
            return pixel;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
