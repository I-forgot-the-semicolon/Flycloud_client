package es.rubencordero.FlyCloud.Sessions;

import es.rubencordero.FlyCloud.ConnectionManager;
import es.rubencordero.FlyCloud.FileManager;
import es.rubencordero.FlyCloud.FlyCloud_Main;
import es.rubencordero.FlyCloud.Utils.Status;
import org.apache.commons.cli.ParseException;
import security.AES;

import java.io.*;
import java.nio.charset.Charset;

public class Session
{
    private String username;

    public Session(String username)
    {
        this.username = username;
    }

    public void NewAccount(String password) throws ParseException
    {

        if(username == null || password == null)
        {
            throw new ParseException("The username and the password are required");
        }

        ConnectionManager clientManager = new ConnectionManager();
        clientManager.Connect("192.168.2.18", 698);

        clientManager.SendNumber(Status.Modes.NewAccount.ordinal());
        clientManager.SendString(username);
        clientManager.SendString(AES.Encrypt(password, AES.RandomKey()));
        clientManager.SendString(AES.randomHash);

        System.out.println("Creating new account with: " + username + " password: " + password);

        int answer = clientManager.GetNumber();
        if (answer == Status.Codes.OK.ordinal())
        {
            System.out.println("Account created!");
            clientManager.CloseConnection();
            String data = "username="+username;
            byte[] binData = data.getBytes(Charset.defaultCharset());
            FileManager fileManager = new FileManager("data.bin");
            fileManager.Write(binData);
        }
        else
        {
            System.out.println("Account already exists!");
        }
    }

    public void Login(String password) throws ParseException
    {
        if(username == null || password == null)
        {
            throw new ParseException("The username and the password are required");
        }

        ConnectionManager clientManager = new ConnectionManager();
        clientManager.Connect("192.168.2.18", 698);

        clientManager.SendNumber(Status.Modes.Login.ordinal());
        clientManager.SendString(username);

        int userExists = clientManager.GetNumber();
        if(userExists == Status.Codes.OK.ordinal())
        {
            String hash = clientManager.GetString();
            System.out.println("Hash: " + hash);
            clientManager.SendString(AES.Encrypt(password, hash));
            int status = clientManager.GetNumber();
            if(status == Status.Codes.OK.ordinal())
            {
                System.out.println("Logged!");
                //Crear binario que contenga el nombre de usuario
                SessionData sessionData = new SessionData(username);
                CreateBinary(sessionData);
            }
            else if(status == Status.Codes.AlreadyLogged.ordinal())
            {
                System.out.println("Already logged!");
            }
            else
            {
                System.out.println("Incorrect Password!");
            }
        }
        else
        {
            System.out.println("Account not exists!");
        }

    }

    private void CreateBinary(SessionData sessionData)
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream file = new ObjectOutputStream(baos);
            file.writeObject(sessionData);
            byte[] bytesOutput = baos.toByteArray();
            file.close();
            baos.close();

            FileOutputStream outputStream = new FileOutputStream("data.bin");
            outputStream.write(bytesOutput);
            outputStream.close();
        }
        catch (IOException e)
        {
            System.out.println("Error: " + e);
        }
    }

    public static SessionData GetSession()
    {
        SessionData session = null;
        try
        {
            File tmpFile = new File("data.bin");
            int length = (int)tmpFile.length();
            byte[] inputArray = new byte[length];
            FileInputStream inputStream = new FileInputStream("data.bin");
            inputStream.read(inputArray);
            inputStream.close();

            ByteArrayInputStream baos = new ByteArrayInputStream(inputArray);
            ObjectInputStream file = new ObjectInputStream(baos);
            session = (SessionData) file.readObject();
            file.close();
            baos.close();
        }
        catch (IOException | ClassNotFoundException e)
        {
            System.out.println("Error: " + e);
        }
        return session;
    }
}
