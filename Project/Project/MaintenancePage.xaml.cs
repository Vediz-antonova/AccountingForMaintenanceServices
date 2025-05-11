using Project.Serializers;
using AccountingForMaintenanceServicesLogic.BusinessLayer;
using AccountingForMaintenanceServicesLogic.DataLayer;
namespace Project;

public partial class MaintenancePage : ContentPage
{
    private readonly string _carFilePath = Path.Combine(FileSystem.AppDataDirectory, "car.json");
    private readonly string _maintenanceFilePath = Path.Combine(FileSystem.AppDataDirectory, "maintenance.json");
    private readonly JsonSerializerService _jsonSerializerService = new JsonSerializerService();

    private readonly CarService _carService = new CarService();
    private readonly MaintenanceService _maintenanceService = new MaintenanceService();
    private readonly int _userId;

    public MaintenancePage(int userId)
    {
        InitializeComponent();
        _userId = userId;
        var cars = LoadCars();
        var maintenances = LoadMaintenances();
        foreach (var maintenance in maintenances)
        {
            _maintenanceService.AddMaintenance(maintenance);
        }
        foreach(Car car in cars)
        { 
            _carService.AddCar(car);
        }
        CarPicker.ItemsSource = cars
            .Where(car => car.UserId == _userId)
            .Select(car => $"{car.Brand}, {car.Model} {car.Year} (ID: {car.Id})")
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
            DisplayAlert("Успех", "Новый автомобиль успешно создан", "OK");
        };

        await Navigation.PushModalAsync(popupPage);
    }

    private async void OnCarDeleteClicked(object sender, EventArgs e)
    {
        var userCars = _carService.GetCarsByUserId(_userId).ToList();

        var carOptions = userCars
            .Select(car => $"{car.Brand}, {car.Model} {car.Year} (ID: {car.Id})")
            .ToArray();

        var action = await DisplayActionSheet("Выберите авто для удаления", "Отмена", null, carOptions);
        if (action != "Отмена")
        {
            _carService.DeleteCar(ExtractCarId(action));
        }
        
        var cars = _carService.GetCarsByUserId(_userId);
        CarPicker.ItemsSource = cars
            .Select(car => $"{car.Brand}, {car.Model} {car.Year}")
            .ToList();
        SaveCars(cars);
    }

    private async void OnMaintenanceClicked(object sender, EventArgs e)
    {
        if (CarPicker.SelectedItem == null)
        {
            await DisplayAlert("Ошибка", "Выберите автомобиль", "OK");
            return;
        }

        var carId = ExtractCarId(CarPicker.SelectedItem.ToString());
        var category = await DisplayActionSheet("Выберите категорию работы", "Отмена", null, "Плановые", "Внеплановые");
    
        if (category == "Отмена" || string.IsNullOrEmpty(category))
            return;

        List<string> maintenanceTypes = category == "Плановые"
            ? new List<string> { "Замена масла", "Техническое обслуживание", "Диагностика двигателя" }
            : new List<string> { "Переобувка", "Замена тормозных колодок", "Ремонт подвески" };

        var selectedType = await DisplayActionSheet("Выберите тип работы", "Отмена", null, maintenanceTypes.ToArray());
    
        if (selectedType == "Отмена" || string.IsNullOrEmpty(selectedType))
            return;

        var popupPage = new CreateMaintenancePopupPage(carId, selectedType, _maintenanceService);
        popupPage.MaintenanceCreated += (s, newMaintenance) =>
        {
            _maintenanceService.AddMaintenance(newMaintenance);
            var maintenances = _maintenanceService.GetAllMaintenances();
            SaveMaintenances(maintenances);
            DisplayAlert("Успех", "Работа успешно добавлена", "OK");
            UpdateMaintenanceListForSelectedCar();
        };

        await Navigation.PushModalAsync(popupPage);
    }
    
    private void CarPicker_SelectedIndexChanged(object sender, EventArgs e)
    {
        UpdateMaintenanceListForSelectedCar();
    }
    
    private void UpdateMaintenanceListForSelectedCar()
    {
        if (CarPicker.SelectedItem == null)
        {
            MaintenanceCollectionView.ItemsSource = null;
            return;
        }

        int carId = ExtractCarId(CarPicker.SelectedItem.ToString());
        var maintenances = _maintenanceService.GetAllMaintenances()
            .Where(m => m.CarId == carId)
            .OrderBy(m => m.CreatedAt) 
            .ToList();

        MaintenanceCollectionView.ItemsSource = maintenances;
    }
    
    private async void MaintenanceCollectionView_SelectionChanged(object sender, SelectionChangedEventArgs e)
    {
        if (e.CurrentSelection?.FirstOrDefault() is Maintenance selectedMaintenance)
        {
            string details = $"Дата: {selectedMaintenance.Date:dd.MM.yyyy}\n" +
                             $"Тип работы: {selectedMaintenance.Category}\n" +
                             $"Пробег: {selectedMaintenance.Mileage}\n" +
                             $"Запчасть: {selectedMaintenance.PartNumber}\n" +
                             $"Стоимость: {selectedMaintenance.Cost}\n" +
                             $"Примечание: {selectedMaintenance.Note}";

            bool delete = await DisplayAlert("Подробности", details, "Удалить", "Закрыть");
            if (delete)
            {
                _maintenanceService.DeleteMaintenance(selectedMaintenance.Id);
                SaveMaintenances(_maintenanceService.GetAllMaintenances());
                UpdateMaintenanceListForSelectedCar();
            }

            ((CollectionView)sender).SelectedItem = null;
        }
    }
    
    private async void OnMaintenanceDeleteInvoked(object sender, EventArgs e)
    {
        if (sender is SwipeItem swipe && swipe.CommandParameter is Maintenance maintenance)
        {
            bool confirm = await DisplayAlert("Удаление", "Вы действительно хотите удалить запись?", "Да", "Нет");
            if (confirm)
            {
                _maintenanceService.DeleteMaintenance(maintenance.Id);
                SaveMaintenances(_maintenanceService.GetAllMaintenances());
                UpdateMaintenanceListForSelectedCar();
            }
        }
    }
    
    protected override void OnAppearing()
    {
        base.OnAppearing();
        UpdateMaintenanceListForSelectedCar();
    }
    
    private List<Car> LoadCars()
    {
        if (File.Exists(_carFilePath))
        {
            // File.Delete(_carFilePath);
            if (!File.Exists(_carFilePath))
            {
                File.Create(_carFilePath).Dispose();
                return new List<Car>();
            }
            var json = File.ReadAllText(_carFilePath);
            return _jsonSerializerService.Deserialize<List<Car>>(json);
        }
        return new List<Car>();
    }
    private void SaveCars(List<Car> cars)
    {
        var json = _jsonSerializerService.Serialize(cars);
        File.WriteAllText(_carFilePath, json);
    }
    
    private List<Maintenance> LoadMaintenances()
    {
        if (File.Exists(_carFilePath))
        {
            // File.Delete(_maintenanceFilePath);
            if (!File.Exists(_maintenanceFilePath))
            {
                File.Create(_maintenanceFilePath).Dispose();
                return new List<Maintenance>();
            }
            var json = File.ReadAllText(_maintenanceFilePath);
            return _jsonSerializerService.Deserialize<List<Maintenance>>(json);
        }
        return new List<Maintenance>();
    }
    private void SaveMaintenances(List<Maintenance> maintenances)
    {
        var json = _jsonSerializerService.Serialize(maintenances);
        File.WriteAllText(_maintenanceFilePath, json);
    }
    
    private int ExtractCarId(string? selectedItem)
    {
        var startIndex = selectedItem.LastIndexOf("ID: ", StringComparison.Ordinal) + 4;
        var endIndex = selectedItem.LastIndexOf(')');
        if (startIndex > 3 && endIndex > startIndex && int.TryParse(selectedItem.Substring(startIndex, endIndex - startIndex), out int id))
        {
            return id;
        }
        throw new Exception("Не удалось извлечь ID автомобиля.");
    }
}