package com.bluejupiter.bookgen;

import com.adobe.epubcheck.api.EpubCheck;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * The class will be used to generated a epub or kindle format e-book
 */
public class EpubGen {

    private String epubFile;
    private String kindleFile;
    private String txtFile;
    private String kindlegen;
    
    private String pattern;

    private Book book;

    private Vector<String> toc;

    private String tempFolder = "./output";

    private String encoding = "gbk";

    private boolean isConvertToKindle = true;

    private EpubGen(){
        book = new Book();
        decideKindleGen();
        pattern = "(第(一|二|三|四|五|六|七|八|九|十|百|[0-9])+(章|卷|节|回|补))(.*)";
        toc = new Vector<String>();
    }

    public EpubGen(String txtFile) {
        this();
        this.txtFile = txtFile;
        int indexofslash = txtFile.lastIndexOf("/");
        int indexofpoint = txtFile.lastIndexOf(".");
        book.setTitle(txtFile.substring(indexofslash + 1, indexofpoint).trim());
        epubFile = txtFile.substring(0, indexofslash).trim() + "/" + book.getTitle() + ".epub";
    }
//    /**
//     * just for converting epub to kindle format
//     * @param input - epub file
//     * @param output - kindle file
//     */
//    public EpubGen(String input, String output) {
//        this.epubFile = input;
//        this.kindleFile = output;
//        if(input == null){
//            input = tempFolder + "";
//        }
//        if(output == null){
//            output = "./test.epub";
//        }
//        decideKindleGen();
//        pattern = "(第(一|二|三|四|五|六|七|八|九|十|百|[0-9])+(章|卷|节|回|补))(.*)";
//    }

    private void decideKindleGen() {
        String os = System.getProperty("os.name");
        if(os.contains("Win")){
            kindlegen = "./bin/kindlegen_v2.8.exe";
        }else {
            kindlegen = "./bin/kindlegen";
        }
    }

    public static void main(String[] args) throws IOException {
//        EpubGen gen = new EpubGen(tempFolder + "","./test.epub");
//        gen.generatedEpub();
//        gen.checkEpub();
//        gen.setKindlegen("./bin/kindlegen_v2.8");//uncommment it in win_os
//        gen.converToMobi();
//        String os = System.getProperty("os.name");
//        if(os.contains("Win")) {
//            System.out.println(os);
//        }
    }

    public void setKindlegen(String kindlegen){
        this.kindlegen = kindlegen;
    }


    public void converToMobi() throws IOException {

        Process process = Runtime.getRuntime().exec(kindlegen + " " + epubFile);
        InputStream fis = process.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line = null;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }

//        System.out.println(flag);
//        int flag = process.exitValue(); //可以通过flag来判断命令是否执行成功
    }

    public void generatedEpub() throws IOException {
        byte[] buffer = new byte[1024];
//        kindleFile = "./test.epub";
        File zipFile = new File(epubFile);
        FileOutputStream fos = new FileOutputStream(zipFile);
        ZipOutputStream zos = new ZipOutputStream(fos);

        System.out.println("Output to Zip : " + zipFile);
        this.writeMimeType(zos);
        ZipEntry container = new ZipEntry("META-INF/container.xml");
        zos.putNextEntry(container);
        FileInputStream inMime2 = new FileInputStream(tempFolder + File.separator + "META-INF/container.xml");
        int len2;
        while((len2 = inMime2.read(buffer)) > 0){
            zos.write(buffer, 0, len2);
        }
        inMime2.close();
        File f = new File(tempFolder + File.separator +"OEBPS");
        List<String> fileList = new ArrayList<String>();
        if(f.exists() && f.isDirectory()) {
            File ff[] = f.listFiles();

            if (ff != null) {
                for (File aFf : ff) {
                    fileList.add(aFf.getName());
                }
            }
        }

        for(String file : fileList){
            if(!file.equals("mimetype") && !file.equals("META-INF/container.xml")){
                System.out.println("File Added : " + file);
                ZipEntry ze= new ZipEntry("OEBPS/"+file);
                zos.putNextEntry(ze);

                FileInputStream in =
                        new FileInputStream(tempFolder + File.separator +"OEBPS" + File.separator + file);

                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }

                in.close();
            }
        }

        zos.closeEntry();
        zos.close();
    }

    public void checkEpub() {
        File epubFile = new File(this.epubFile);

        // simple constructor; errors are printed on stderr stream
        EpubCheck epubcheck = new EpubCheck(epubFile);

        // validate() returns true if no errors or warnings are found
        Boolean result = epubcheck.validate();
        System.out.println(result?"valid epub": "not valid epub");
    }

    private void writeMimeType(ZipOutputStream zip) throws IOException {
        byte[] content = "application/epub+zip".getBytes("UTF-8");
        ZipEntry entry = new ZipEntry("mimetype");
        entry.setMethod(ZipEntry.STORED);
        entry.setSize(20);
        entry.setCompressedSize(20);
        entry.setCrc(0x2CAB616F); // pre-computed
        zip.putNextEntry(entry);
        zip.write(content);
        zip.closeEntry();
    }

    public String readStringFromFile(File file,String encoding) {
        StringBuilder sb = new StringBuilder();
        if (!file.exists()) {
            System.out.println("文件不存在");
            return null;
        }
        // 检查文件名是否符合要求,这一步暂时省略......................................
        BufferedReader br = null;
        try {
            InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);
            br = new BufferedReader(read);

            String string;
            while ((string = br.readLine()) != null) {
                if(string.trim().length() >0) {
                    sb.append(string);
                    sb.append("\r\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
        return sb.toString();
    }



    private void generatedFile(String str, String file){
        PrintWriter pw = null;
        try {
            File fileOfSave = new File(file);
            fileOfSave.createNewFile();
            pw = new PrintWriter(fileOfSave);
            pw.print(str);
            pw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (pw != null)
                pw.close();
        }
    }

    private void generatedTitles(){
        File file = new File(this.txtFile);

        // 检查文件名是否符合要求,这一步暂时省略......................................
        BufferedReader br;
        try {
            InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
            br = new BufferedReader(read);

            String string;
            String title = "前言";
            toc.add(title);
            while ((string = br.readLine()) != null) {
//                System.out.println(string);
                string = this.removeSpace(string);
                if (string != null && string.trim().length() > 0) {
                    title = this.getTitle(string);
                    if(title != null){
                        toc.add(title);
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public String getTitle(String input){
        Pattern p=Pattern.compile(pattern);

        Matcher m=p.matcher(input);


        if(m.find()){
            System.out.println(m.group());
            return this.removeSpace(m.group());

//            i++;
        }

        return null;
    }

    public String removeSpace(String input){
        return input.replaceAll("^[　| |    ]+", "");
    }

    public void generateEpub(Book book){
        File file = new File(this.txtFile);
        if (prepare(file)) return;

        //read template for chapter
        String template = this.readStringFromFile(new File("./template/OEBPS/chapter_temp.html"), "utf-8");

        StringBuilder sb = new StringBuilder();
        // 检查文件名是否符合要求,这一步暂时省略......................................
        BufferedReader br = null;
        try {
            InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);
            br = new BufferedReader(read);

            String string;
            String title = "前言";
            int count = 0;
            String chapter = template.replaceAll("_chaptertitle_", "" + title);
            while ((string = br.readLine()) != null) {
                string = this.removeSpace(string);
                if(string != null && string.trim().length() >0) {
                    String titles = this.getTitle(string);
                    if(titles!= null&& this.toc.contains(titles)) {
                        title = titles;
                        String content = sb.toString();

                        content = java.util.regex.Matcher.quoteReplacement(content);
                        try {
                            chapter = chapter.replaceAll("_body_", content);
                        }catch(Exception e){
                            System.out.println(content);
                            e.printStackTrace();

                        }

                        String fileN = "chapter" + count;
                        String fN = fileN + ".html";

                        this.generatedFile(chapter, tempFolder + "/OEBPS/" +
                                fN);
                        count++;
                        sb = new StringBuilder();

                        chapter = template.replaceAll("_chaptertitle_","" + title);
                    }else {
                        sb.append("<p>");
                        sb.append("　　" + string.replaceAll("[&|<|\\|/|;]", ""));
                        sb.append("</p>");
                        sb.append("\r\n");
                    }

                }
            }
            String content = sb.toString();
            chapter = chapter.replaceAll("_body_",content);
            this.generatedFile(chapter, tempFolder + "/OEBPS/" +
                    "chapter" + count + ".html");

            createOthers(book);
//            EpubGen gen = new EpubGen(tempFolder + "","./test.epub");
            this.generatedEpub();
            this.checkEpub();
//        gen.setKindlegen("./bin/kindlegen_v2.8");
            if(isConvertToKindle)//only it is true, will convert to mobi
                this.converToMobi();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
//        return sb.toString();
//
    }

    private boolean prepare(File file) {
        if (!file.exists()) {
            System.out.println("文件不存在");
            return true;
        }
        //Clear the kindleFile folder
        File f = new File(tempFolder);
        if(f.exists() && f.isDirectory()){
            File[] fList = f.listFiles();
            for(int i= 0;i< fList.length;i++){
                File oebps = fList[i];
                if(oebps.exists() && oebps.isDirectory()){
                    File[] files = oebps.listFiles();
                    for(int j=0;j< files.length;j++){
                        files[j].delete();
                    }
                }
                fList[i].delete();
            }
        }else{
            try {
                f.mkdir();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        File meta = new File(tempFolder + "/META-INF");
        meta.mkdir();
        File oebps = new File(tempFolder + "/OEBPS");
        oebps.mkdir();
        this.copyFile("./template/mimetype", tempFolder + "/mimetype");
        this.copyFile("./template/OEBPS/style.css", tempFolder + "/OEBPS/style.css");
        this.copyFile("./template/OEBPS/style.css", tempFolder + "/OEBPS/style.css");
        this.copyFile("./template/META-INF/container.xml", tempFolder + "/META-INF/container.xml");
        return false;
    }

    private void createOthers(Book book){
        StringBuilder navSb = new StringBuilder();
        StringBuilder itemRefsb = new StringBuilder();
        StringBuilder refsb = new StringBuilder();
        StringBuilder bookToc = new StringBuilder();
        int count = 0;
        for (int i = 0; i < this.toc.size(); i++) {
            String fileN = "chapter" + count;
            String fN = fileN + ".html";
            String navPoint = this.generateNavPoint(fileN, count + 3, fN, this.toc.get(i));
            navSb.append(navPoint);
            itemRefsb.append(this.generateItemRef(fileN));
            refsb.append(this.generateItemId(fileN, fN));
            bookToc.append(this.generateBookToc(this.toc.get(i), fN));
            count++;
        }
        createContentOpf(itemRefsb.toString(), refsb.toString());
        createToc(book.getTitle(),navSb.toString());
        createCover(book);
        createBookToc(bookToc.toString());
    }

    private void createToc(String title,String toc){
        String cover = this.readStringFromFile(new File("./template/OEBPS/toc_temp.ncx"), "utf-8");
        cover = cover.replaceAll("_title_",title);
        cover = cover.replaceAll("_navPoint_", toc);
        this.generatedFile(cover, tempFolder+"/OEBPS/toc.ncx");
    }

    private void createBookToc(String toc) {
        String cover = this.readStringFromFile(new File("./template/OEBPS/book-toc.html"), "utf-8");

        cover = cover.replaceAll("_toc_",toc);
        this.generatedFile(cover, tempFolder+"/OEBPS/book-toc.html");
    }


    private void createCover(Book book) {
        String cover = this.readStringFromFile(new File("./template/OEBPS/cover_temp.html"), "utf-8");
        cover = cover.replaceAll("_title_", book.getTitle());
        cover = cover.replaceAll("_author_",book.getAuthor() == null ? "":book.getAuthor());
        this.generatedFile(cover, tempFolder +"/OEBPS/cover.html");
    }

    private void createContentOpf(String itemRef, String ref) {
        String cover = this.readStringFromFile(new File("./template/OEBPS/content_temp.opf"), "utf-8");
        cover = cover.replaceAll("_title_",book.getTitle());
        cover = cover.replaceAll("_itemref_",itemRef);
        cover = cover.replaceAll("_manifest_",ref);
        this.generatedFile(cover, tempFolder+"/OEBPS/content.opf");
    }

    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }


    private String generateNavPoint(String chapter, int num, String chaptherFile, String title){
        return "<navPoint id=\"" +
                chapter +
                "\" playOrder=\"" +
                num +
                "\">\r\n"+
                "<navLabel><text>" +
                title +
                "</text></navLabel>\r\n" +
                "<content src=\"" +
                chaptherFile +
                "\"/>" +
                "\r\n</navPoint>\r\n";

    }

    private String generateItemRef(String chaptherFile){
        return "<itemref idref=\"" +
                chaptherFile +
                "\" linear=\"yes\"/>\r\n";
    }
    private String generateItemId(String fileN,String fN) {
        return "<item id=\"" +
                fileN +
                "\" href=\"" +
                fN +
                "\" media-type=\"application/xhtml+xml\"/>\r\n";

    }

    private String generateBookToc(String title,String fN){
        return "<dt class=\"tocl2\"><a href=\"" +
                fN +
                "\">" +
                title +
                "</a></dt>\r\n";
    }

    public String getEpubFile() {
        return epubFile;
    }

    public void setEpubFile(String epubFile) {
        this.epubFile = epubFile;
    }

    public String getKindleFile() {
        return kindleFile;
    }

    public void setKindleFile(String kindleFile) {
        this.kindleFile = kindleFile;
    }

    public String getTxtFile() {
        return txtFile;
    }

    public void setTxtFile(String txtFile) {
        this.txtFile = txtFile;
    }

    public String getKindlegen() {
        return kindlegen;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Vector<String> getToc() {
        if(toc == null) toc = new Vector<String>();
        if(toc.size() == 0) this.generatedTitles();
        return toc;
    }

    public void setToc(Vector<String> toc) {
        this.toc = toc;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public boolean isConvertToKindle() {
        return isConvertToKindle;
    }

    public void setConvertToKindle(boolean isConvertToKindle) {
        this.isConvertToKindle = isConvertToKindle;
    }
}
