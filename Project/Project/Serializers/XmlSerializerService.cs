using System.Xml.Serialization;
namespace AccountingForMaintenanceServicesProject.Serializers;

public class XmlSerializerService : ISerializer
{
    public string Serialize<T>(T data)
    {
        using var stringWriter = new StringWriter();
        var serializer = new XmlSerializer(typeof(T));
        serializer.Serialize(stringWriter, data);
        return stringWriter.ToString();
    }

    public T Deserialize<T>(string serializedData)
    {
        using var stringReader = new StringReader(serializedData);
        var serializer = new XmlSerializer(typeof(T));
        return (T)serializer.Deserialize(stringReader);
    }
}