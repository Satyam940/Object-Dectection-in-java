package org.example;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.Scanner;


import java.io.*;
import java.sql.*;
import java.util.Date;

public class Main {
    public static void main(String[] args) throws InterruptedException, SQLException, IOException {

        Connection con = null;
        Statement st=null;
        connect ob = null;
        ResultSet rs = null;
        int Count = 0;
        try{
            ob = new connect();
            con=ob.getConnection();
            st = con.createStatement();
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }

        Date currentDate = new Date();

        // Print current date and time
        System.out.println("Current Date and Time: " + currentDate);
        String imgPath = "C:\\Users\\mkmis\\IdeaProjects\\object\\captured_image.jpg";
        File file = new File(imgPath);
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);


        // Open the default camera
        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.out.println("Error: Camera not opened");
            return;
        }

        // Capture a frame from the camera
        Mat frame = new Mat();
        if (camera.read(frame)) {
            // Save the captured frame to a file
            String imagePath = "captured_image.jpg";
            Imgcodecs.imwrite(imagePath, frame);
            System.out.println(imagePath);
        } else {
            System.out.println("Error: Couldn't capture frame");
        }

        // Release the camera
        camera.release();



        System.setProperty("webdriver.chrome.driver", "F:\\python\\chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        ChromeOptions warning = new ChromeOptions();

        warning.setCapability("goog:loggingPrefs", "{\"browser\": \"SEVERE\"}");


        WebDriver driver = new ChromeDriver(options);




        driver.get("https://image-reader399.vercel.app/");
        String filePath="C:\\Users\\mkmis\\IdeaProjects\\object\\captured_image.jpg";

        driver.findElement(By.xpath("//*[@id=\"root\"]/div/input[1]")).sendKeys("tell me what is in my hand in 2 or 3 word");

        WebElement fileInput = driver.findElement(By.xpath("//*[@id=\"root\"]/div/input[2]"));
        fileInput.sendKeys(filePath);
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/button")).click();


        Thread.sleep(10000);
        WebElement divElement = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div"));

        String text = divElement.getText();

        System.out.println(text);




        String query = "INSERT INTO object_detection(Description, Time) VALUES (?, ?)";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setString(1, text);
        pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));

        pstmt.executeUpdate();







        Scanner scanner = new Scanner(System.in);
        System.out.print("Do you want CSV file press y");
        String ans = scanner.nextLine();
        String data = "SELECT * FROM object_detection";

        // CSV file path
        String csvFilePath = "output.csv";

        if ("y".equals(ans) || "Y".equals(ans)) {


            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydatabase", "root", "");
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(data);
                 FileWriter csvWriter = new FileWriter(csvFilePath)) {

                // Write column headers to CSV file
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    csvWriter.append(metaData.getColumnName(i));
                    if (i < columnCount) {
                        csvWriter.append(",");
                    } else {
                        csvWriter.append("\n");
                    }
                }

                // Write data rows to CSV file
                while (resultSet.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        csvWriter.append(resultSet.getString(i));
                        if (i < columnCount) {
                            csvWriter.append(",");
                        } else {
                            csvWriter.append("\n");
                        }
                    }
                }

                System.out.println("Data has been exported to the CSV file successfully!");

            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }


            if (file.exists()) {

                if (file.delete()) {
                    System.out.println(" ");

                }
            }

            driver.quit();
        }
    }
        }
