package es.rubencordero.FlyCloud;

import java.util.Scanner;

import es.rubencordero.FlyCloud.Sessions.Session;
import es.rubencordero.FlyCloud.Sessions.SessionData;
import es.rubencordero.FlyCloud.Utils.Status;
import org.apache.commons.cli.*;

public class FlyCloud_Main
{

    public static void main(String[] args)
    {
        ParserController parserController = new ParserController();

        try
        {
            CommandLine cmd = parserController.getCmdParser().parse(parserController.getOptions(), args);
            if(cmd.hasOption("register") && cmd.hasOption("u") && cmd.hasOption("p"))
            {
                Session session = new Session(cmd.getOptionValue("u"));
                session.NewAccount(cmd.getOptionValue("p"));
            }
            else if(cmd.hasOption("login") && cmd.hasOption("u") && cmd.hasOption("p"))
            {
                Session session = new Session(cmd.getOptionValue("u"));
                session.Login(cmd.getOptionValue("p"));
            }
            else if(cmd.hasOption("push"))
            {
                String filename = cmd.getOptionValue("push");
                FileManager fileManager = new FileManager(filename);
                fileManager.OpenToRead();

                //Mode and login check
                SessionData sessionData = Session.GetSession();
                if(sessionData == null)
                {
                    System.out.println("Error reading data file");
                    System.exit(-1);
                }
                else
                {
                    ConnectionManager clientManager = new ConnectionManager();
                    //clientManager.Connect("54.38.188.69", 698);
                    clientManager.Connect("192.168.2.18", 698);
                    clientManager.SendNumber(Status.Modes.Upload.ordinal());
                    clientManager.SendString(sessionData.getUsername());
                    int status = clientManager.GetNumber();
                    if(status == Status.Codes.OK.ordinal())
                    {
                        //Sent stuff
                        clientManager.setFileManager(fileManager);
                        TransferInfo transferInfo = clientManager.SendFileInfo(filename);

                        char answer;
                        Scanner scanner = new Scanner(System.in);
                        System.out.println("Send? y/n");
                        answer = scanner.next().charAt(0);
                        if(answer != 'y')
                            System.exit(0);

                        clientManager.UploadFile(transferInfo);
                    }
                    else
                        System.out.println("You have to login in order to push a file");
                }
            }
            else if(cmd.hasOption("pull"))
            {
                String filename = cmd.getOptionValue("pull");

                ConnectionManager clientManager = new ConnectionManager();
                clientManager.Connect("54.38.188.69", 698);

                System.out.println("Sending: " + filename);
                clientManager.SendString(filename);
                int fileSize = clientManager.GetNumber();
                System.out.println("Filesize: " + fileSize);

                int nPackages = clientManager.GetNumber();
                int sizePackages = clientManager.GetNumber();
                int residuous = clientManager.GetNumber();

                System.out.println("Numero de paquetes: " + nPackages);
                System.out.println("Size per package: " + sizePackages);
                System.out.println("Resto: " + residuous);

                clientManager.ReceiveFile(filename, fileSize, nPackages, sizePackages, residuous);
            }
            else
            {
               if(cmd.getArgs().length != 0)
               {
                   throw new ParseException("Unknown command...");
               }
                parserController.getHelpFormatter().printHelp("->", parserController.getOptions());
            }
        }
        catch (ParseException e)
        {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
