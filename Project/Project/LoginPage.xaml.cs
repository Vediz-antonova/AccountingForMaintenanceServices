using Project.Serializers;
using AccountingForMaintenanceServicesLogic.DataLayer;
namespace Project;

public partial class LoginPage : ContentPage
{
    private readonly string UserFilePath = Path.Combine(FileSystem.AppDataDirectory, "user.json");
    private readonly JsonSerializerService _jsonSerializerService= new JsonSerializerService();
    
    public LoginPage()
    {
        InitializeComponent();
    }

    private void OnLoginClicked(object sender, EventArgs e)
    {
        try
        {
            var users = LoadUsers();
            var user = users.FirstOrDefault(u => u.Email == EmailEntry.Text && u.Password == PasswordEntry.Text);
            if (user != null)
            {
                DisplayAlert("Успех", "Вход выполнен успешно!", "OK");
            }
            else
            {
                DisplayAlert("Ошибка", "Неверный email или пароль", "OK");
            }
        }
        catch (Exception ex)
        {
            DisplayAlert("Ошибка", $"Произошла ошибка: {ex.Message}", "OK");
        }
    }

    private List<User> LoadUsers()
    {
        if (File.Exists(UserFilePath))
        {
            var json = File.ReadAllText(UserFilePath);
            return _jsonSerializerService.Deserialize<List<User>>(json);
        }
        return new List<User>();
    }
}