package easv.dk.eventticketsystem.bll;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.oned.Code128Writer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;



public class QRBarcodeManager {

    /**
     * Generates a random UUID (without dashes), then creates and saves:
     * - a QR code (2D) as PNG in /qr_codes/
     * - a barcode (1D, Code 128) as PNG in /barcodes/
     *
     * @return the unique code (UUID) used in both QR and barcode, to be stored in the database.
     */

    public static String generateAndSaveQRCodeAndBarcode(int QRwidth, int QRheight,int barcodeWidth,int barcodeHeight) throws IOException {

        String uniqueCode = UUID.randomUUID().toString().replace("-", "");
        /// Creates the folder that saves momentarily 2D Barcode
        String qrDirectory = System.getProperty("user.dir") + "/qr_codes/";
        File qrFile = new File(qrDirectory);
        if (!qrFile.exists()) {
            qrFile.mkdirs();
        }
        /// Instantiates the QR path
        String qrPath = qrDirectory + uniqueCode + ".png";

        /// Creates the folder that saves momentarily 1D Barcode
        String barcodeDirectory = System.getProperty("user.dir") + "/barcodes/";
        File barcodeFile  = new File(barcodeDirectory);
        if (!barcodeFile .exists()) {
            barcodeFile .mkdirs();
        }
        /// Instantiates the barcode path
        String barcodePath = barcodeDirectory + uniqueCode + ".png";

        // Generate both QR and barcode
        try {
            generateQRCode(uniqueCode, qrPath, QRwidth, QRheight);
            System.out.println("Saving QR to: " + qrPath);



            generateBarcode(uniqueCode,barcodePath, barcodeWidth, barcodeHeight);
            System.out.println("Saving Barcode to: " + barcodePath);
        } catch (WriterException e) {
            throw new IOException("Failed to generate QR or Barcode: " + e.getMessage(), e);
        }

        return uniqueCode; // return the string stored in Database
    }

    public static void deleteUUIDfiles(String uniqueCode){

        String qrPath = System.getProperty("user.dir") + "/qr_codes/" + uniqueCode + ".png";
        String barcodePath = System.getProperty("user.dir") + "/barcodes/" + uniqueCode + ".png";

        File qrFile = new File(qrPath);
        File barcodeFile = new File(barcodePath);

        if (qrFile.exists()){
            qrFile.delete();
        }

        if (barcodeFile.exists()){
            barcodeFile.delete();
        }
    }



    /**
     * Generates a barcode (1D, Code 128) with no margin and scales it to the specified size.
     * The image is cropped to remove excess whitespace and resized to match desired dimensions.
     */

    public static void generateQRCode(String data, String qrPath, int QRwidth, int QRheight) throws WriterException, IOException {

        /// Removes white border
        Map<EncodeHintType,Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1);


        BitMatrix matrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, QRwidth, QRheight,hints);
        Path path = FileSystems.getDefault().getPath(qrPath);
        MatrixToImageWriter.writeToPath(matrix, "PNG", path);
    }



    public static void generateBarcode(String data, String barcodePath, int barcodeWidth, int barcodeHeight) throws WriterException, IOException {

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 0);


        BitMatrix matrix = new Code128Writer().encode(data, BarcodeFormat.CODE_128, barcodeWidth, barcodeHeight, hints);

      // Removes excessive borders
        int[] rect = matrix.getEnclosingRectangle(); // [x, y, width, height]
        BitMatrix croppedMatrix = new BitMatrix(rect[2], rect[3]);
        for (int y = 0; y < rect[3]; y++) {
            for (int x = 0; x < rect[2]; x++) {
                if (matrix.get(x + rect[0], y + rect[1])) {
                    croppedMatrix.set(x, y);
                }
            }
        }

        BufferedImage croppedImage = MatrixToImageWriter.toBufferedImage(croppedMatrix);
        BufferedImage finalImage = new BufferedImage(barcodeWidth, barcodeHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = finalImage.createGraphics();
        g.drawImage(croppedImage, 0, 0, barcodeWidth, barcodeHeight, null);
        g.dispose();


        Path path = FileSystems.getDefault().getPath(barcodePath);
        MatrixToImageWriter.writeToPath(croppedMatrix, "PNG", path);
        }


    /**
     * Re-generates both the QR code and barcode image files for a given UUID string.
     * Useful after deleting image files but keeping the code in the database.
     * Useful also if we need to load "Confirmed" orders in the future
     */

public static void regenerateQRCodeAndBarcode(String uniqueCode, int QRwidth, int QRheight, int barcodeWidth, int barcodeHeight) throws IOException {
    String qrPath = System.getProperty("user.dir") + "/qr_codes/" + uniqueCode + ".png";
    String barcodePath = System.getProperty("user.dir") + "/barcodes/" + uniqueCode + ".png";

             try {
        generateQRCode(uniqueCode, qrPath, QRwidth, QRheight);
        generateBarcode(uniqueCode, barcodePath, barcodeWidth, barcodeHeight);
        System.out.println("🔁 Regenerated QR and barcode for: " + uniqueCode);
        } catch (WriterException e) {
        throw new IOException("Failed to regenerate QR/barcode: " + e.getMessage(), e);
        }
    }

}





