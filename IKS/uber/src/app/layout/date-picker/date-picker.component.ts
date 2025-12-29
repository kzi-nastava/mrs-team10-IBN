import {ChangeDetectionStrategy, Component } from '@angular/core';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {provideNativeDateAdapter} from '@angular/material/core';

@Component({
  selector: 'app-date-picker',
  imports: [MatFormFieldModule, MatInputModule, MatDatepickerModule],
  templateUrl: './date-picker.component.html',
  styleUrl: './date-picker.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [provideNativeDateAdapter()],


})
export class DatePickerComponent {

}

