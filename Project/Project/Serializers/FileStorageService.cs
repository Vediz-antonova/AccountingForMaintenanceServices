namespace AccountingForMaintenanceServicesProject.Serializers;

public class FileStorageService
{
    private readonly ISerializer _serializer;
    private readonly string _filePath;

    public FileStorageService(ISerializer serializer, string filePath)
    {
        _serializer = serializer;
        _filePath = filePath;
    }

    public void Save<T>(T data)
    {
        string serializedData = _serializer.Serialize(data);
        File.WriteAllText(_filePath, serializedData);
    }

    public T Load<T>()
    {
        if (!File.Exists(_filePath)) return default;
        string serializedData = File.ReadAllText(_filePath);
        return _serializer.Deserialize<T>(serializedData);
    }
}