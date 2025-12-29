import { Component, output } from '@angular/core';
import { FormControl, FormGroup, FormArray, ReactiveFormsModule } from '@angular/forms';
import { Location } from '../../model/location.model';

@Component({
  selector: 'app-ride-approx-form',
  imports: [ReactiveFormsModule],
  templateUrl: './ride-approx-form.component.html',
  styleUrl: './ride-approx-form.component.css',
})
export class RideApproxFormComponent {
  endpoints: FormGroup = new FormGroup({
    startLoc: new FormControl(""),
    destination: new FormControl(""),
    stations: new FormArray<FormControl<string>>([])
  })

  routeOutput = output<Location[]>();

  get stations(){
    return (this.endpoints.get('stations') as FormArray)
  }

  addStation(){
    this.stations.push(new FormControl(""))
  }

  removeStation(){
    this.stations.removeAt(this.stations.length - 1)
  }

  getRoute(){
    let route:Location[] = [];
    let order = 0
    route.push({
      address: this.endpoints.controls['startLoc'].value,
      type: 'pickup',
      index: order
    })
    order += 1
    for (let val of this.stations.getRawValue()){
      route.push({
        address: val,
        type: 'stop',
        index: order
      })
      order += 1
    }
    route.push({
      address: this.endpoints.controls['destination'].value,
      type: 'destination',
      index: order
    })
    console.log(route)
    this.routeOutput.emit(route)
  }
}
