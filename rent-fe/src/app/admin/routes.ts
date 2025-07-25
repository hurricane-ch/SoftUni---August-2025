import {Routes} from '@angular/router';
import {HomeComponent} from "./home.component";

export const routes: Routes = [
    {
        path: "",
        component: HomeComponent,
        children: [
            {
                path: '',
                loadComponent: () =>
                    import("./dashboard/dashboard.component").then(m => m.DashboardComponent),
            },
                        {
                path: 'custom-reservation',
                loadComponent: () =>
                    import("./custom-reservation/custom-reservation.component").then(m => m.CustomReservationComponent),
            },
            {
                path: "dashboard",
                loadComponent: () =>
                    import("./dashboard/dashboard.component").then(m => m.DashboardComponent),
            },
            {
                path: "classifier",
                loadComponent: () =>
                    import("./classifier/classifier.component").then(m => m.ClassifierComponent),
            },
            {
                path: "settlement",
                loadComponent: () =>
                    import("./settlement/settlement.component").then(m => m.SettlementComponent)
            },
            {
                path: "message-resource",
                loadComponent: () =>
                    import("./message-resource/message-resource.component").then(m => m.MessageResourceComponent)
            },
            {
                path: "language",
                loadComponent: () =>
                    import("./language/language.component").then(m => m.LanguageComponent)
            },
            {
                path: "branch",
                loadComponent: () =>
                    import("./branch/branch.component").then(m => m.BranchComponent)
            },
            {
                path: "user",
                loadComponent: () =>
                    import('./user/user.component').then(m => m.UserComponent)
            },
            {
                path: "rental-holder",
                loadComponent: () =>
                    import('./rental-holder/rental-holder.component').then(m => m.RentalHolderComponent)
            },
            {
                path: "rental-items",
                loadComponent: () =>
                    import('./rental-item/rental-item.component').then(m => m.RentalItemComponent)
            },
            {
                path: "rental-items/:id",
                loadComponent: () =>
                    import('./edit-rental-item/edit-rental-item.component').then(m => m.EditRentalItemComponent)
            },
            {
                path: "reservations",
                loadComponent: () =>
                    import('./reservation/reservation.component').then(m => m.ReservationComponent)
            },
            {
                path: "reservation-list",
                loadComponent: () =>
                    import('./reservation-list/reservation-list.component').then(m => m.ReservationListComponent)
            },
            {
                path: "**", redirectTo: "dashboard", pathMatch: "full"
            },
        ],
    },
];