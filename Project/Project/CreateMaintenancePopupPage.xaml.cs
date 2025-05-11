using System.Globalization;
using AccountingForMaintenanceServicesLogic.DataLayer;
using AccountingForMaintenanceServicesLogic.BusinessLayer;
namespace Project;

public partial class CreateMaintenancePopupPage : ContentPage
{
    private readonly int _carId;
    private readonly string _category;
    private readonly MaintenanceService _maintenanceService;

    public event EventHandler<Maintenance>? MaintenanceCreated;

    public CreateMaintenancePopupPage(int carId, string category, MaintenanceService maintenanceService)
    {
        InitializeComponent();
        _carId = carId;
        _category = category;
        _maintenanceService = maintenanceService;
    }

    private async void OnCreateClicked(object sender, EventArgs e)
    {
        if (string.IsNullOrWhiteSpace(DateEntry.Text) ||
            string.IsNullOrWhiteSpace(MileageEntry.Text) ||
            string.IsNullOrWhiteSpace(PartNumberEntry.Text) ||
            string.IsNullOrWhiteSpace(CostEntry.Text) ||
            string.IsNullOrWhiteSpace(NoteEntry.Text))
        {
            await DisplayAlert("Ошибка", "Заполните все поля", "OK");
            return;
        }

        if (!DateTime.TryParseExact(DateEntry.Text, "dd.MM.yyyy", CultureInfo.InvariantCulture, DateTimeStyles.None, out DateTime date))
        {
            await DisplayAlert("Ошибка", "Некорректный формат даты", "OK");
            return;
        }
        if (!int.TryParse(MileageEntry.Text, out int mileage))
        {
            await DisplayAlert("Ошибка", "Некорректный пробег", "OK");
            return;
        }
        if (!decimal.TryParse(CostEntry.Text, out decimal cost))
        {
            await DisplayAlert("Ошибка", "Некорректный пробег", "OK");
            return;
        }

        var maintenances = _maintenanceService.GetAllMaintenances().ToList();
        int newMaintenanceId = (maintenances.Any() ? maintenances.Max(m => m.Id) : 0) + 1;

        var newMaintenance = new Maintenance(newMaintenanceId, _carId, date, mileage, _category, PartNumberEntry.Text.Trim(), cost, NoteEntry.Text.Trim());
        MaintenanceCreated?.Invoke(this, newMaintenance);
        await Navigation.PopModalAsync();
    }

    private async void OnCancelClicked(object sender, EventArgs e)
    {
        await Navigation.PopModalAsync();
    }
}