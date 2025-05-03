using AccountingForMaintenanceServicesLogic.BusinessLayer;
using AccountingForMaintenanceServicesLogic.DataLayer;
namespace Project;

public partial class CreateCarPopupPage : ContentPage
{
    private readonly int _userId;
    private readonly CarService _carService;

    public event EventHandler<Car> CarCreated;

    public CreateCarPopupPage(int userId, CarService carService)
    {
        InitializeComponent();
        _userId = userId;
        _carService = carService;
    }

    private async void OnCreateClicked(object sender, EventArgs e)
    {
        if (string.IsNullOrWhiteSpace(BrandEntry.Text) ||
            string.IsNullOrWhiteSpace(ModelEntry.Text) ||
            string.IsNullOrWhiteSpace(YearEntry.Text) ||
            string.IsNullOrWhiteSpace(MileageEntry.Text))
        {
            await DisplayAlert("Ошибка", "Заполните все поля", "OK");
            return;
        }

        if (!int.TryParse(YearEntry.Text, out int year))
        {
            await DisplayAlert("Ошибка", "Некорректный год", "OK");
            return;
        }

        if (!int.TryParse(MileageEntry.Text, out int mileage))
        {
            await DisplayAlert("Ошибка", "Некорректный пробег", "OK");
            return;
        }

        var userCars = _carService.GetCarsByUserId(_userId).ToList();
        int newCarId = (userCars.Any() ? userCars.Max(car => car.Id) : 0) + 1;

        var newCar = new Car(newCarId, _userId, BrandEntry.Text.Trim(), ModelEntry.Text.Trim(), year, mileage);
        CarCreated?.Invoke(this, newCar);
        await Navigation.PopModalAsync();
    }

    private async void OnCancelClicked(object sender, EventArgs e)
    {
        await Navigation.PopModalAsync();
    }
}