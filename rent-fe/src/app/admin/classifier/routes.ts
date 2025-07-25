import {Routes} from "@angular/router";
import {ClassifierComponent} from "./classifier.component";
import {ClassifierListComponent} from "./components/classifier-list/classifier-list.component";
import {ClassifierItemComponent} from "./components/classifier-item/classifier-item.component";

export const routes: Routes = [
    {
        path: '',
        component: ClassifierComponent,
        children: [
            {
                path: '',
                component: ClassifierListComponent,
            },
            {
                path: ':id',
                component: ClassifierItemComponent,
            },
        ],
    },
];