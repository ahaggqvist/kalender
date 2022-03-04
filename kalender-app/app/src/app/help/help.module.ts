import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {SharedModule} from '../shared/shared.module';
import {HelpComponent} from './help/help.component';

const routes: Routes = [
  {
    path: '',
    component: HelpComponent
  }
];

@NgModule({
  imports:
    [
      SharedModule,
      RouterModule.forChild(routes)
    ],
  exports: [],
  declarations:
    [
      HelpComponent
    ],
  providers: []
})
export class HelpModule {
}
