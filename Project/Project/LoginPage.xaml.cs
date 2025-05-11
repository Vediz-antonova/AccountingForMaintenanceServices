using Project.Serializers;
using AccountingForMaintenanceServicesLogic.DataLayer;
namespace Project;

public partial class LoginPage : ContentPage
{
    private readonly string _userFilePath = Path.Combine(FileSystem.AppDataDirectory, "user.json");
    private readonly JsonSerializerService _jsonSerializerService= new JsonSerializerService();
    
    public LoginPage()
    {
        InitializeComponent();
    }

    private async void OnLoginClicked(object sender, EventArgs e)
    {
        try
        {
            var users = LoadUsers();
            // var user = users.FirstOrDefault(u => u.Email == EmailEntry.Text && u.Password == PasswordEntry.Text);
            var user = users.FirstOrDefault();
            if (user != null)
            {
                _ = DisplayAlert("Успех", "Вход выполнен успешно!", "OK");
                await Navigation.PushAsync(new MaintenancePage(user.Id));
            }
            else
            {
                _ = DisplayAlert("Ошибка", "Неверный email или пароль", "OK");
            }
        }
        catch (Exception ex)
        {
            _ = DisplayAlert("Ошибка", $"Произошла ошибка: {ex.Message}", "OK");
        }
    }

    private List<User> LoadUsers()
    {
        if (File.Exists(_userFilePath))
        {
            var json = File.ReadAllText(_userFilePath);
            return _jsonSerializerService.Deserialize<List<User>>(json);
        }
        return new List<User>();
    }
}