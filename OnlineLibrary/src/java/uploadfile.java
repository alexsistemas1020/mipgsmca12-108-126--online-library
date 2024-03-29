/*
Document   : uploadfile.java
Created on : Jun 7, 2012, 9:17:45 AM
Author     : Sathish
 */
import P.DBHandler;
import java.io.*;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "uploadfile", urlPatterns = "/uploadfile")
public class uploadfile extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try{
        PrintWriter out = response.getWriter();
        String contentType = request.getContentType();
           if ((contentType != null) && (contentType.indexOf("multipart/form-data") >= 0)) {
            DataInputStream in = new DataInputStream(request.getInputStream());
            int formDataLength = request.getContentLength();
            byte dataBytes[] = new byte[formDataLength];
            int byteRead = 0;
            int totalBytesRead = 0;
            while (totalBytesRead < formDataLength) {
                byteRead = in.read(dataBytes, totalBytesRead, formDataLength);
                totalBytesRead += byteRead;
            }
            String file = new String(dataBytes);
            String saveFile = file.substring(file.indexOf("filename=\"") + 10);
            saveFile = saveFile.substring(0, saveFile.indexOf("\n"));
            saveFile = saveFile.substring(saveFile.lastIndexOf("\\") + 1, saveFile.indexOf("\""));
            int lastIndex = contentType.lastIndexOf("=");
            String boundary = contentType.substring(lastIndex + 1, contentType.length());
            int pos;
            pos = file.indexOf("filename=\"");
            pos = file.indexOf("\n", pos) + 1;
            pos = file.indexOf("\n", pos) + 1;
            pos = file.indexOf("\n", pos) + 1;
            int boundaryLocation = file.indexOf(boundary, pos) - 4;
            int startPos = ((file.substring(0, pos)).getBytes()).length;
            int endPos = ((file.substring(0, boundaryLocation)).getBytes()).length;
            DBHandler d=new DBHandler();
            if(d.isDocumentExist(saveFile)==1)
            throw new Exception("<br><br>Sorry,<br><br>A file with this name already exist in the server.<br><br>Please try again with different name.");    
            if(saveFile.substring(saveFile.lastIndexOf(".")+1,saveFile.length()).equalsIgnoreCase("exe"))
            throw new Exception("<br><br>Sorry,<br><br>Execution (.exe) files does not allowed to upload because of security purpose.");
            int i=d.insertDocument(saveFile);
            saveFile = "D:\\" + saveFile;
            FileOutputStream fileOut = new FileOutputStream(saveFile);
            fileOut.write(dataBytes, startPos, (endPos - startPos));
            fileOut.flush();
            fileOut.close();
            d.createIndex(saveFile);
            out.println("<br><h1>Congratulations!<br><br>Your file uploded into the server successfully.<br><br>Your file id is:"+i);
        } else {
            throw new Exception("Please Select a file.");
        }

    }
        catch(Exception e)
        {
        response.sendRedirect("Ex.jsp?hidden="+e.toString());
        }
        }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
