using Project.Serializers;
using AccountingForMaintenanceServicesLogic.DataLayer;
using System.Text.RegularExpressions;
namespace Project;

public partial class RegisterPage : ContentPage
{
    private readonly string UserFilePath = Path.Combine(FileSystem.AppDataDirectory, "user.json");
    private readonly JsonSerializerService _jsonSerializerService= new JsonSerializerService();

    public RegisterPage()
    {
        InitializeComponent();
    }

    private void OnRegisterClicked(object sender, EventArgs e)
    {
        try
        {
            if (string.IsNullOrWhiteSpace(EmailEntry.Text) || string.IsNullOrWhiteSpace(PasswordEntry.Text))
            {
                DisplayAlert("Ошибка", "Все поля должны быть заполнены", "OK");
                return;
            }

            if (!IsValidEmail(EmailEntry.Text))
            {
                DisplayAlert("Ошибка", "Введите корректный email", "OK");
                return;
            }

            if (PasswordEntry.Text.Length < 8)
            {
                DisplayAlert("Ошибка", "Пароль должен содержать не менее 8 символов", "OK");
                return;
            }

            var users = LoadUsers();
            if (users.Any(u => u.Email == EmailEntry.Text))
            {
                DisplayAlert("Ошибка", "Пользователь с таким email уже зарегистрирован", "OK");
                return;
            }

            var newUserId = users.Any() ? users.Max(u => u.Id) + 1 : 0;
            var newUser = new User(newUserId, EmailEntry.Text, PasswordEntry.Text);
            users.Add(newUser);
            SaveUsers(users);
            DisplayAlert("Успех", "Регистрация прошла успешно!", "OK");
        }
        catch (Exception ex)
        {
            DisplayAlert("Ошибка", $"Произошла ошибка: {ex.Message}", "OK");
        }
    }
    
    private bool IsValidEmail(string email)
    {
        var emailRegex = new Regex(@"^[^@\s]+@[^@\s]+\.[^@\s]+$");
        return emailRegex.IsMatch(email);
    }
    
    private List<User> LoadUsers()
    {
        if (File.Exists(UserFilePath))
        {
            if (!File.Exists(UserFilePath))
            {
                File.Create(UserFilePath).Dispose();
                return new List<User>();
            }
            var json = File.ReadAllText(UserFilePath);
            return _jsonSerializerService.Deserialize<List<User>>(json);
        }
        return new List<User>();
    }
    private void SaveUsers(List<User> users)
    {
        var json = _jsonSerializerService.Serialize(users);
        File.WriteAllText(UserFilePath, json);
    }
}