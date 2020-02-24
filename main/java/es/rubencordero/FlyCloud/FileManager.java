package es.rubencordero.FlyCloud;

import java.io.*;

public class FileManager
{
    private String fileName;
    private FileInputStream inputStream;
    private FileOutputStream outputStream;
    private long fileSize;


    public FileManager(String fileName)
    {
        this.fileName = fileName;
    }

    public void OpenToRead()
    {
        try
        {
            File file = new File(fileName);
            inputStream = new FileInputStream(fileName);
            fileSize = file.length();
        }
        catch (FileNotFoundException err)
        {
            System.out.println("No se pudo encontrar el archivo");
            System.exit(-1);
        }
    }

    public void Write(byte[] data)
    {
        try
        {
            outputStream = new FileOutputStream(fileName);
            outputStream.write(data);
            outputStream.close();
        }
        catch (IOException err)
        {
            System.out.println("Error creating the data file");
            System.exit(-2);
        }
    }

    private void Close()
    {
        try
        {
            inputStream.close();
        }
        catch (IOException err)
        {
            System.out.println("Error al cerrar el archivo");
            System.exit(-1);
        }
    }

    public byte[] ReadBytes(int size)
    {
        byte[] buffer = new byte[size];
        try
        {
            inputStream.read(buffer);
        }
        catch (IOException e)
        {
            System.out.println("Error al leer...");
            System.exit(-6);
        }
        return buffer;
    }

    public void JumpToPosition(int position)
    {
        try
        {
            System.out.println("Jumping to " + position);
            inputStream.getChannel().position(position);
        }
        catch (IOException err)
        {
            System.out.println("Error al saltar a la posicion: " + err);
            System.exit(-6);
        }
    }
    public long getFileSize()
    {
        return fileSize;
    }
}
