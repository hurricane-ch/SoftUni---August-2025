import { Routes } from "@angular/router";
import { PageNotFoundComponent } from "./admin/components/page-not-found/page-not-found.component";
import { AuthGuard } from "./guards/auth.guard";
import { LandingComponent } from "./pages/landing/landing.component";
import { ContactUsComponent } from "./pages/contact-us/contact-us.component";
import { EntertainmentComponent } from "./pages/entertainment/entertainment.component";
import { RentalItemListComponent } from "./pages/reservation/rental-item-list.component";
import { FoodComponent } from "./pages/food/food.component";
import { GalleryComponent } from "./pages/gallery/gallery.component";
import { RentalItemComponent } from "./pages/reservation/rental-items-list/rental-item/rental-item.component";

export const routes: Routes = [
  {
    path: "",
    component: LandingComponent,
  },
  {
    path: "entertainment",
    component: EntertainmentComponent,
  },
  {
    path: "food",
    component: FoodComponent,
  },
  {
    path: "gallery",
    component: GalleryComponent,
  },
  {
    path: "contact-us",
    component: ContactUsComponent,
  },
  {
    path: "reservation",
    component: RentalItemListComponent,
  },
  {
    path: "rental-item/:id",
    component: RentalItemComponent,
  },
  {
    path: "auth",
    loadChildren: () => import("./auth/routes").then((m) => m.routes),
  },
  {
    path: "admin",
    loadChildren: () => import("./admin/routes").then((m) => m.routes),
    canActivate: [AuthGuard],
  },
  {
    path: "**",
    component: PageNotFoundComponent,
  },
];
