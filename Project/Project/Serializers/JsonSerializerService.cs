using System.Text.Json;
namespace Project.Serializers;

public class JsonSerializerService : ISerializer
{
    public string Serialize<T>(T data) => JsonSerializer.Serialize(data);
    public T Deserialize<T>(string serializedData) => JsonSerializer.Deserialize<T>(serializedData);
}