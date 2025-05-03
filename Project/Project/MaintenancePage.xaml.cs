using Project.Serializers;
using AccountingForMaintenanceServicesLogic.BusinessLayer;
using AccountingForMaintenanceServicesLogic.DataLayer;
namespace Project;

public partial class MaintenancePage : ContentPage
{
    private readonly string CarFilePath = Path.Combine(FileSystem.AppDataDirectory, "car.json");
    private readonly JsonSerializerService _jsonSerializerService= new JsonSerializerService();

    private readonly CarService _carService = new CarService();
    private Car _selectedCar;
    private readonly int _userId;

    public MaintenancePage(int userId)
    {
        InitializeComponent();
        _userId = userId;
        var cars = LoadCars();
        foreach(Car car in cars)
        {
            if (car.UserId == _userId)
                _carService.AddCar(car);
        }
        CarPicker.ItemsSource = cars
            .Where(car => car.UserId == _userId)
            .Select(car => $"{car.Brand}, {car.Model} {car.Year}")
            .ToList();
    }

    private async void OnCarCreateClicked(object sender, EventArgs e)
    {
        var popupPage = new CreateCarPopupPage(_userId, _carService);
        popupPage.CarCreated += (s, newCar) =>
        {
            _carService.AddCar(newCar);
            var cars = _carService.GetCarsByUserId(_userId);
            CarPicker.ItemsSource = cars
                .Select(car => $"{car.Brand}, {car.Model} {car.Year}")
                .ToList();
            SaveCars(cars);
            Device.BeginInvokeOnMainThread(() => DisplayAlert("Успех", "Новый автомобиль успешно создан", "OK"));
        };

        await Navigation.PushModalAsync(popupPage);
    }

    private async void OnCarDeleteClicked(object sender, EventArgs e)
    {
        var userCars = _carService.GetCarsByUserId(_userId).ToList();

        var carOptions = userCars
            .Select(car => $"{car.Brand} (ID: {car.Id})")
            .ToArray();

        var action = await DisplayActionSheet("Выберите авто для удаления", "Отмена", null, carOptions);
        if (action != "Отмена")
        {
            var startIndex = action.LastIndexOf("ID: ") + 4;
            var endIndex = action.LastIndexOf(')');
            if (startIndex > 3 && endIndex > startIndex)
            {
                var idStr = action.Substring(startIndex, endIndex - startIndex);
                if (int.TryParse(idStr, out int carId))
                {
                    _carService.DeleteCar(carId);
                }
            }
        }
        
        var cars = _carService.GetCarsByUserId(_userId);
        CarPicker.ItemsSource = cars
            .Select(car => $"{car.Brand}, {car.Model} {car.Year}")
            .ToList();
        SaveCars(cars);
    }
    
    private List<Car> LoadCars()
    {
        if (File.Exists(CarFilePath))
        {
            // File.Delete(CarFilePath);
            if (!File.Exists(CarFilePath))
            {
                File.Create(CarFilePath).Dispose();
                return new List<Car>();
            }
            var json = File.ReadAllText(CarFilePath);
            return _jsonSerializerService.Deserialize<List<Car>>(json);
        }
        return new List<Car>();
    }
    private void SaveCars(List<Car> cars)
    {
        var json = _jsonSerializerService.Serialize(cars);
        File.WriteAllText(CarFilePath, json);
    }
}