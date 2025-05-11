using Project.Serializers;
using AccountingForMaintenanceServicesLogic.DataLayer;
using System.Text.RegularExpressions;
namespace Project;

public partial class RegisterPage
{
    private readonly string _userFilePath = Path.Combine(FileSystem.AppDataDirectory, "user.json");
    private readonly JsonSerializerService _jsonSerializerService= new JsonSerializerService();

    public RegisterPage()
    {
        InitializeComponent();
    }

    private async void OnRegisterClicked(object sender, EventArgs e)
    {
        try
        {
            if (string.IsNullOrWhiteSpace(EmailEntry.Text) || string.IsNullOrWhiteSpace(PasswordEntry.Text))
            {
                _ = DisplayAlert("Ошибка", "Все поля должны быть заполнены", "OK");
                return;
            }

            if (!IsValidEmail(EmailEntry.Text))
            {
                _ = DisplayAlert("Ошибка", "Введите корректный email", "OK");
                return;
            }

            if (PasswordEntry.Text.Length < 8)
            {
                _ = DisplayAlert("Ошибка", "Пароль должен содержать не менее 8 символов", "OK");
                return;
            }

            var users = LoadUsers();
            if (users.Any(u => u.Email == EmailEntry.Text))
            {
                _ = DisplayAlert("Ошибка", "Пользователь с таким email уже зарегистрирован", "OK");
                return;
            }

            var newUserId = users.Any() ? users.Max(u => u.Id) + 1 : 0;
            var newUser = new User(newUserId, EmailEntry.Text, PasswordEntry.Text);
            users.Add(newUser);
            SaveUsers(users);
            _ = DisplayAlert("Успех", "Регистрация прошла успешно!", "OK");
            await Navigation.PopAsync();
        }
        catch (Exception ex)
        {
            _ = DisplayAlert("Ошибка", $"Произошла ошибка: {ex.Message}", "OK");
        }
    }
    
    private bool IsValidEmail(string email)
    {
        var emailRegex = new Regex(@"^[^@\s]+@[^@\s]+\.[^@\s]+$");
        return emailRegex.IsMatch(email);
    }
    
    private List<User> LoadUsers()
    {
        if (File.Exists(_userFilePath))
        {
            // File.Delete(UserFilePath);
            if (!File.Exists(_userFilePath))
            {
                File.Create(_userFilePath).Dispose();
                return new List<User>();
            }
            var json = File.ReadAllText(_userFilePath);
            return _jsonSerializerService.Deserialize<List<User>>(json);
        }
        return new List<User>();
    }
    private void SaveUsers(List<User> users)
    {
        var json = _jsonSerializerService.Serialize(users);
        File.WriteAllText(_userFilePath, json);
    }
}