namespace Project;

public partial class MapPage : ContentPage
{
    private int _userId;
    public MapPage(int userId)
    {
        InitializeComponent();
        _userId = userId;
    }
    
    private async void OnMaintenanceClicked(object sender, EventArgs e)
    {
        await Navigation.PushAsync(new MaintenancePage(_userId));
    }
    private async void OnNotificationsClicked(object sender, EventArgs e)
    {
        await Navigation.PushAsync(new NotificationsPage(_userId));
    }
    private async void OnReportsClicked(object sender, EventArgs e)
    {
        await Navigation.PushAsync(new ReportsPage(_userId));
    }
}