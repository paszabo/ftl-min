package ftlmin.test;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;



public class Test {

   public static void main(String[] args) {
      String js = "<script src=\"...\"></script>\n<script>\n// a comment\n/*another comment*/\n\nalert(\"hi\");</script>";
      
      System.out.println(js);
      
      HtmlCompressor compressor = new HtmlCompressor();
      
      compressor.setCompressJavaScript(true);
      
      System.out.println(compressor.isCompressJavaScript());
      
      String compressedJs = compressor.compress(js);
      
      System.out.println(compressedJs);
   }
   
}
