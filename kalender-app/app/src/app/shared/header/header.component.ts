import {Component, OnDestroy, OnInit} from '@angular/core';
import {MenuItem} from 'primeng/api';
import {Subscription} from 'rxjs';
import {AccountService} from '../../core/services/account.service';
import Account from '../../interfaces/account';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, OnDestroy {
  private sub: Subscription;
  avatar: string = '??';
  userInfo: string;
  items: MenuItem[];

  constructor(private accountService: AccountService) {
  }

  ngOnInit(): void {
    this.items = [
      {label: 'Select calendar', routerLink: '/', routerLinkActiveOptions: {exact: true}},
      {label: 'Help', routerLink: 'help', routerLinkActiveOptions: {exact: true}}
    ];

    this.sub = this.accountService.findAccount().subscribe(
      (account: Account) => {
        this.avatar = `${account.firstName.substring(0, 1)}${account.lastName.substring(0, 1)}`;
        this.userInfo = `${account.firstName} ${account.lastName} (${account.username})`
      });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }
}
