package Corretor;

import java.awt.image.BufferedImage;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

public class OpenCV {
    
    static{
        //-Djava.library.path="D:\opencv\build\java\x86"
        //System.load("D:\\opencv\\build\\java\\x86\\opencv_java330.dll");
        //E:\NetBeansProjects\CorretorAutomatico\FINAL
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //System.load(System.getProperty("user.dir")+"\\opencv_java330.dll");
        //System.out.println(System.getProperty("user.dir")+"\\opencv_java330.dll");
        if(System.getProperty("sun.arch.data.model").equals("32")){
            System.load(System.getProperty("user.dir")+"\\X86"+"\\opencv_java330.dll");
        }else{
            System.load(System.getProperty("user.dir")+"\\X64"+"\\opencv_java330.dll");
        }
    }
    
    public static BufferedImage Faz(String entrada){
        Mat img = Imgcodecs.imread(entrada);
        Imgproc.resize(img,img,new Size(2481,3908)); //3508
        
        Point pt = new Point(img.cols()/2, img.rows()/2);                         
        Mat M = Imgproc.getRotationMatrix2D(pt, 180, 1.0);     
        Imgproc.warpAffine( img, img, M, img.size());
        
        
        Rect rect = new Rect(0, 0, img.width()-1, img.height()-1000); // 1500
        //Imgproc.rectangle(img, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar(170,0,150,0), 5);
        //img = img.submat(rect);
        img = new Mat(img, rect);
        
        pt = new Point(img.cols()/2, img.rows()/2);                         
        M = Imgproc.getRotationMatrix2D(pt, 180, 1.0);     
        Imgproc.warpAffine( img, img, M, img.size());
        
        for(int i=0;i<img.height();i++){            
            double[] data = img.get(i, 0);
            data[0] = 255;
            data[1] = 255;
            data[2] = 255;
            img.put(i, 0, data);          
        }
        
        for(int i=0;i<img.width();i++){            
            double[] data = img.get(0, i);
            data[0] = 255;
            data[1] = 255;
            data[2] = 255;
            img.put(0, i, data);          
        }
        
        //Imgcodecs.imwrite("C:\\Users\\MATHEUS\\Desktop\\gabInicial.jpg",img);
        img = filter(img);
        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(img, img, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
        //Imgcodecs.imwrite("C:\\Users\\MATHEUS\\Desktop\\gabFinal.jpg",img);
        
        BufferedImage ok = new BufferedImage(2481,3508,5);
        ok = matToBufferedImage(img,ok);
        
        return ok;
    }
        
    public static BufferedImage Faz(BufferedImage entrada){     
        Mat img = bufferedImageToMat(entrada);
        Imgproc.resize(img,img,new Size(2481,3508)); 
        img = filter(img);
        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(img, img, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
        //Imgcodecs.imwrite("C:\\Users\\MATHEUS\\Desktop\\gabFinal.jpg",img);
        
        BufferedImage ok = new BufferedImage(2481,3508,5);
        ok = matToBufferedImage(img,ok);
        
        return ok;
    }
    
    public static String removeAccents(String str) {
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        str = str.replaceAll("[^\\p{ASCII}]", "");
        return str;
    }
    
    private static Mat bufferedImageToMat(BufferedImage in) {
        Mat out;
        byte[] data;
        int r, g, b;

        if (in.getType() == BufferedImage.TYPE_INT_RGB) {
            out = new Mat(in.getHeight(), in.getWidth(), CvType.CV_8UC3);
            data = new byte[in.getWidth() * in.getHeight() * (int) out.elemSize()];
            int[] dataBuff = in.getRGB(0, 0, in.getWidth(), in.getHeight(), null, 0, in.getWidth());
            for (int i = 0; i < dataBuff.length; i++) {
                data[i * 3] = (byte) ((dataBuff[i] >> 0) & 0xFF);
                data[i * 3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
                data[i * 3 + 2] = (byte) ((dataBuff[i] >> 16) & 0xFF);
            }
        } else {
            out = new Mat(in.getHeight(), in.getWidth(), CvType.CV_8UC1);
            data = new byte[in.getWidth() * in.getHeight() * (int) out.elemSize()];
            int[] dataBuff = in.getRGB(0, 0, in.getWidth(), in.getHeight(), null, 0, in.getWidth());
            for (int i = 0; i < dataBuff.length; i++) {
                r = (byte) ((dataBuff[i] >> 0) & 0xFF);
                g = (byte) ((dataBuff[i] >> 8) & 0xFF);
                b = (byte) ((dataBuff[i] >> 16) & 0xFF);
                data[i] = (byte) ((0.21 * r) + (0.71 * g) + (0.07 * b));
            }
        }
        out.put(0, 0, data);
        return out;
    }
        
    public static Mat filter(final Mat src) {
       
        final Mat dst = new Mat(src.rows(), src.cols(), src.type());
        Mat image = new Mat(src.rows(), src.cols(), src.type());
        src.copyTo(image);
        src.copyTo(dst);

        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_BGR2GRAY);
 
        //convert the image to black and white does (8 bit)
        Imgproc.threshold(dst, dst, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
        Mat temp = dst.clone();
        //find the contours
        Mat hierarchy = new Mat();

        Mat corners = new Mat(4,1,CvType.CV_32FC2);
        List<MatOfPoint> points = new ArrayList<MatOfPoint>();
        Imgproc.findContours(temp, points,hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        hierarchy.release();
        
        for (int idx = 0; idx < points.size(); idx++){
            MatOfPoint contour = points.get(idx);
            MatOfPoint2f contour_points = new MatOfPoint2f(contour.toArray());
            RotatedRect minRect = Imgproc.minAreaRect( contour_points );
            Point[] rect_points = new Point[4];
            minRect.points( rect_points );
            if(minRect.size.height > src.width() / 2){
                List<Point> srcPoints = new ArrayList<Point>(4);
                
                if(rect_points[2].x > rect_points[0].x){
                    srcPoints.add(rect_points[1]);//primeiro ponto
                    srcPoints.add(rect_points[2]);//segundo ponto
                    srcPoints.add(rect_points[3]);//quarto ponto
                    srcPoints.add(rect_points[0]);//terceiro ponto
                }else{
                    srcPoints.add(rect_points[2]);//primeiro ponto
                    srcPoints.add(rect_points[3]);//segundo ponto
                    srcPoints.add(rect_points[0]);//quarto ponto
                    srcPoints.add(rect_points[1]);//terceiro ponto    
                }

                corners = Converters.vector_Point_to_Mat(srcPoints, CvType.CV_32F);

            }

        }
        
        List<Point> dstPoints = new ArrayList<Point>(4);
        dstPoints.add(new Point(0, 0));
        dstPoints.add(new Point(1000, 0));
        dstPoints.add(new Point(1000, 250));
        dstPoints.add(new Point(0, 250));
        Mat quad_pts = Converters.vector_Point_to_Mat(dstPoints, CvType.CV_32F);
        Mat results = new Mat(1000,250,CvType.CV_8UC3);
        Mat quad = new Mat(1000,250,CvType.CV_8UC1);
        
        Mat transmtx = Imgproc.getPerspectiveTransform(corners, quad_pts);
        Imgproc.warpPerspective( src, results, transmtx, new Size(1000,250));
        Imgproc.warpPerspective( src, quad, transmtx, new Size(1000,250));

        Imgproc.resize(quad,quad,new Size(1365,1000));
        
        return quad;
        
    }
    
    public static BufferedImage matToBufferedImage(Mat matrix, BufferedImage bimg){
        if ( matrix != null ) { 
            int cols = matrix.cols();  
            int rows = matrix.rows();  
            int elemSize = (int)matrix.elemSize();  
            byte[] data = new byte[cols * rows * elemSize];  
            int type;  
            matrix.get(0, 0, data);  
            switch (matrix.channels()) {  
            case 1:  
                type = BufferedImage.TYPE_BYTE_GRAY;  
                break;  
            case 3:  
                type = BufferedImage.TYPE_3BYTE_BGR;  
                // bgr to rgb  
                byte b;  
                for(int i=0; i<data.length; i=i+3) {  
                    b = data[i];  
                    data[i] = data[i+2];  
                    data[i+2] = b;  
                }  
                break;  
            default:  
                return null;  
            }  

            // Reuse existing BufferedImage if possible
            if (bimg == null || bimg.getWidth() != cols || bimg.getHeight() != rows || bimg.getType() != type) {
                bimg = new BufferedImage(cols, rows, type);
            }        
            bimg.getRaster().setDataElements(0, 0, cols, rows, data);
        } else { // mat was null
            bimg = null;
        }
        return bimg;  
    }   

}
