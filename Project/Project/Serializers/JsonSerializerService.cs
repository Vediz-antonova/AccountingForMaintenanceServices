using System.Text.Json;
namespace AccountingForMaintenanceServicesProject.Serializers;

public class JsonSerializerService : ISerializer
{
    public string Serialize<T>(T data) => JsonSerializer.Serialize(data);
    public T Deserialize<T>(string serializedData) => JsonSerializer.Deserialize<T>(serializedData);
}