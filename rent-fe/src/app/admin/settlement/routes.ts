import {Routes} from "@angular/router";
import {SettlementComponent} from "./settlement.component";
import {SettlementListComponent} from "./components/settlement-list/settlement-list.component";
import {SettlementItemComponent} from "./components/settlement-item/settlement-item.component";

export const routes: Routes = [
    {
        path: '',
        component: SettlementComponent,
        children: [
            {
                path: '',
                component: SettlementListComponent,
            },
            {
                path: ':code',
                component: SettlementItemComponent,
            },
        ],
    },
];