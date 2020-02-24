package es.rubencordero.FlyCloud;

public class TransferInfo
{
    private FileManager fileManager;
    private int nPackages;
    private int sizePackages;
    private int remainder;

    TransferInfo(FileManager fileManager, int nPackages, int sizePackages, int remainder)
    {
        this.fileManager = fileManager;
        this.nPackages = nPackages;
        this.sizePackages = sizePackages;
        this.remainder = remainder;
    }

    public FileManager getfileManager() {
        return fileManager;
    }

    public int getnPackages() {
        return nPackages;
    }

    public int getSizePackages() {
        return sizePackages;
    }

    public int getRemainder() {
        return remainder;
    }
}
