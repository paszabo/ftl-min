package org.vwc.ftlmin;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.googlecode.htmlcompressor.compressor.ClosureJavaScriptCompressor;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;


public class JsMinDirective implements TemplateDirectiveModel {
   
   public void execute(Environment env, @SuppressWarnings("rawtypes") Map params, TemplateModel[] loopVars,
         TemplateDirectiveBody body) throws TemplateException, IOException {
     // enforce no parameters for now
     if (! params.isEmpty()) {
         throw new TemplateModelException(
                 "@jsmin doesn't allow parameters.");
     }
     if (loopVars.length != 0) {
             throw new TemplateModelException(
                 "@jsmin doesn't allow loop variables.");
     }
     
     
     /* Renders the body of the directive body to the specified writer. The writer is not flushed after the rendering.
         If you pass the environment's writer, there is no need to flush it. If you supply your own writer, you are 
         responsible to flush/close it when you're done with using it (which might be after multiple renderings).  */
     
     // if the body isn't empty
     if (body != null) {
    	 // buffer the directive body into a string
         Writer w = new StringWriter();
         body.render(w);
         String buffer = w.toString();
         w.flush();
         w.close();
         
         // minify the buffered string
         String min = minify(buffer);
         body.render(new UpperCaseFilterWriter(env.getOut(), min));
     } else {
         throw new RuntimeException("missing body");
     }
 }
   
   private String minify(String s) throws IOException {
       HtmlCompressor compressor = new HtmlCompressor();
       compressor.setCompressJavaScript(true);
       compressor.setCompressCss(true);
       compressor.setRemoveIntertagSpaces(true);
       compressor.setRemoveMultiSpaces(true);
       compressor.setRemoveComments(true);
       compressor.setRemoveMultiSpaces(true);
       compressor.setYuiJsPreserveAllSemiColons(true);
       
       String compressed = compressor.compress(s);

       return compressed;
    }
   
   /**
    * A {@link Writer} that transforms the character stream to upper case
    * and forwards it to another {@link Writer}.
    */ 
   
   private static class UpperCaseFilterWriter extends Writer {
      
       private final Writer out;
       private final String text;
       private boolean hasWritten = false;
          
       UpperCaseFilterWriter (Writer out, String text) {
           this.out = out;
           this.text = text;
       }

       public void write(char[] cbuf, int off, int len) throws IOException {
    	   if (!hasWritten)
    	   { out.write(text); hasWritten = true; }
       }

       public void flush() throws IOException {
           out.flush();
       }

       public void close() throws IOException {
           out.close();
       }
   }
}
