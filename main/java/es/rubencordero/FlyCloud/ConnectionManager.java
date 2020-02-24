package es.rubencordero.FlyCloud;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionManager
{
    public ServerSocket serverSocket;
    public Socket localSocket;
    public DataInputStream inputStream;
    private DataOutputStream outputStream;
    private int bytesReceived;
    private FileManager fileManager;

    public ConnectionManager()
    {
        System.out.println("Creating connection manager");
    }

    public void StartServer(int port)
    {
        try
        {
            serverSocket = new ServerSocket(port);
            localSocket = serverSocket.accept();
            localSocket.setTcpNoDelay(true);
            inputStream = new DataInputStream(new BufferedInputStream(localSocket.getInputStream()));
            outputStream = new DataOutputStream(localSocket.getOutputStream());
        }
        catch (IOException err)
        {
            System.out.println("Error: " + err);
            System.exit(-1);
        }

    }

    public void CloseServer()
    {
        try
        {
            serverSocket.close();
            inputStream.close();
            outputStream.close();
        }
        catch (IOException err)
        {
            System.out.println("Error: " + err);
            System.exit(-1);

        }
    }

    public void CloseConnection()
    {
        try
        {
            inputStream.close();
            outputStream.close();
            localSocket.close();
        }
        catch (IOException err)
        {
            System.out.println("Error: " + err);
            System.exit(-1);
        }
    }

    public void Connect(String ip, int port)
    {
        try
        {
            localSocket = new Socket(ip, port);
            inputStream = new DataInputStream(new BufferedInputStream(localSocket.getInputStream()));
            outputStream = new DataOutputStream(localSocket.getOutputStream());
        }
        catch (IOException err)
        {
            System.out.println("Error: " + err);
            System.exit(-1);
        }
    }

    public int GetNumber()
    {
        int number = 0;
        try
        {
            number = inputStream.readInt();
            //System.out.println("Recibido: " + number);
        }
        catch (IOException err)
        {
            System.out.println("Error at reading: " + err);
            System.exit(-1);
        }
        return number;
    }

    public void SendNumber(int number)
    {
        try
        {
            outputStream.writeInt(number);
            //System.out.println("Sending: " + number);
        } catch (IOException err)
        {
            System.out.println("Error: " + err);
            System.exit(-1);
        }
    }

    public char GetChar()
    {
        char inputChar = ' ';
        try
        {
            inputChar = inputStream.readChar();
        }
        catch (IOException err)
        {
            System.out.println("Error: " + err);
            System.exit(-1);
        }
        return inputChar;
    }

    public void SendChar(char character)
    {
        try
        {
            outputStream.writeChar(character);
        }
        catch (IOException err)
        {
            System.out.println("Error: " + err);
            System.exit(-1);
        }
    }

    public String GetString()
    {
        String string = null;
        int filenameSize = GetNumber();
        byte[] buffer = new byte[filenameSize];
        try
        {
            int bytesReceived = inputStream.read(buffer);
            string = new String(buffer);
        }
        catch (IOException err)
        {
            System.out.println("Error: " + err);
            System.exit(-5);
        }
        return string;
    }

    public void SendString(String string)
    {
        SendNumber(string.length());
        byte[] buffer = string.getBytes();
        try
        {
            outputStream.write(buffer);
        }
        catch (IOException err)
        {
            System.out.println("Error: " + err);
            System.exit(-5);
        }
    }

    public byte[] GetBytes(int size)
    {
        byte[] tmpBuffer = new byte[size];
        byte[] buffer = new byte[size];
        try
        {
            bytesReceived = inputStream.read(tmpBuffer);
            buffer = new byte[bytesReceived];
            //System.out.println("Bytes received: " + bytesReceived);
            //System.out.println("Copying array... ");

            System.arraycopy(tmpBuffer, 0, buffer, 0, bytesReceived);
        }
        catch (IOException err)
        {
            System.out.println("Error al recibir datos binarios: " + err);
            System.exit(-6);
        }
        return buffer;
    }

    public void SendBytes(byte[] bytes)
    {
        try
        {
            outputStream.write(bytes);
        }
        catch (IOException err)
        {
            System.out.println("Error al enviar datos binarios: " + err);
            System.exit(-6);
        }
    }

    public TransferInfo SendFileInfo(String filename)
    {
        int fileSize = (int)fileManager.getFileSize();
        System.out.println("Sending size: " + fileSize);
        SendNumber(fileSize);
        System.out.println("Sending name: " + filename);
        SendString(filename);

        int nPackages = GetNumber();
        int sizePackages = GetNumber();
        int residuous = GetNumber();

        System.out.println("Numero de paquetes: " + nPackages);
        System.out.println("Size per package: " + sizePackages);
        System.out.println("Resto: " + residuous);

        return new TransferInfo(fileManager, nPackages, sizePackages, residuous);
    }

    public void UploadFile(TransferInfo transferInfo)
    {
        FileManager fileManager = transferInfo.getfileManager();
        int nPackages = transferInfo.getnPackages();
        int sizePackages = transferInfo.getSizePackages();
        int residuous = transferInfo.getRemainder();
        int fileSize = (int)fileManager.getFileSize();

        double averageSpeed, countSpeed = 0;
        int sumSpeed = 0, packagesCount = 0;
        long len = 0, startProgram = System.currentTimeMillis();

        while (len < fileSize)
        {
            int chunkSize = (packagesCount == nPackages-1 && residuous != 0) ? residuous : sizePackages;
            System.out.println("-----------------------");
            System.out.println("Package: " + (packagesCount+1) + " / " + nPackages);

            byte[] buffer = fileManager.ReadBytes(chunkSize);

            System.out.println("Enviando: " + chunkSize + " Bytes");
            long startTime = System.currentTimeMillis();
            SendBytes(buffer);

            int realBytesSent = GetNumber();
            len += realBytesSent;
            packagesCount++;

            double actualTime = System.currentTimeMillis()-startTime;
            if(actualTime > 0)
            {
                countSpeed += realBytesSent/actualTime;
            }
            sumSpeed++;
            averageSpeed = countSpeed/sumSpeed*1000;
            if(averageSpeed > 0)
                System.out.println("Average Transfer speed: " + Math.round(averageSpeed/Math.pow(2,20)) + " MiB/s " + Math.round(averageSpeed/Math.pow(2,10)) + " KiB/s");
            System.out.println((Math.round(System.currentTimeMillis()-startProgram)/1000) + " of " + Math.round(fileManager.getFileSize()/averageSpeed) + " seconds");
        }
        System.out.println("Enviado en " + ((System.currentTimeMillis()-startProgram)/1000) + " segundos");
    }

    public void ReceiveFile(String filename, int fileSize, int nPackages, int sizePackages, int residuous)
    {
        try
        {
            OutputStream outputStream = new FileOutputStream(filename);
            int i = 0, len = 0;

            while(len < fileSize)
            {
                int size = (i == nPackages-1 && residuous != 0) ? residuous : sizePackages;
                System.out.println("Getting package number: " + (i+1) + " expecting a size of " + size);

                int bytesReceived = 0;
                byte[] buffer;
                while(bytesReceived != size)
                {
                    buffer = GetBytes(size);
                    int tmp = getBytesReceived();
                    bytesReceived += tmp;
                    //SendNumber(tmp);
                    System.out.println("Bytes received: " + bytesReceived + " / " + size);
                    outputStream.write(buffer);
                }

                SendNumber(bytesReceived);

                len += bytesReceived;
                i++;
                System.out.println(len + "/" + fileSize);
            }
        }
        catch (FileNotFoundException err)
        {
            System.out.println("Error al abrir archivo: " + err);
            System.exit(-10);
        } catch (IOException err)
        {
            System.out.println("Error al escribir archivo: " + err);
            System.exit(-10);
        }
    }


    public int getBytesReceived()
    {
        return bytesReceived;
    }

    public void setFileManager(FileManager fileManager)
    {
        this.fileManager = fileManager;
    }

    public FileManager getFileManager()
    {
        return fileManager;
    }

}
