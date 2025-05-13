using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Project;

public partial class ReportsPage : ContentPage
{
    private int _userId;
    public ReportsPage(int userId)
    {
        InitializeComponent();
        _userId = userId;
    }
    
    private async void OnMaintenanceClicked(object sender, EventArgs e)
    {
        await Navigation.PushAsync(new MaintenancePage(_userId));
    }
    private async void OnMapClicked(object sender, EventArgs e)
    {
        await Navigation.PushAsync(new MapPage(_userId));
    }
    private async void OnNotificationsClicked(object sender, EventArgs e)
    {
        await Navigation.PushAsync(new NotificationsPage(_userId));
    }
}