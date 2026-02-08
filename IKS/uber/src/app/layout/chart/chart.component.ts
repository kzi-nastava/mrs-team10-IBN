import { Component, Input, OnInit, ViewChild, ElementRef, OnChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, ChartConfiguration, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-chart',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="chart-wrapper">
      <canvas #chartCanvas></canvas>
    </div>
  `,
  styles: [
    `
      .chart-wrapper {
        position: relative;
        height: 200px;
        width: 100%;
      }
    `,
  ],
})
export class ChartComponent implements OnInit, OnChanges {
  @Input() data: { date: string; value: number }[] = [];
  @Input() label: string = 'Data';
  @Input() type: 'line' | 'bar' = 'line';
  @Input() color: string = '#4CAF50';
  @Input() yAxisLabel: string = '';

  @ViewChild('chartCanvas', { static: true }) chartCanvas!: ElementRef<HTMLCanvasElement>;

  private chart: Chart | null = null;

  ngOnInit() {
    this.createChart();
  }

  ngOnChanges() {
    if (this.chart) {
      this.updateChart();
    }
  }

  createChart() {
    const ctx = this.chartCanvas.nativeElement.getContext('2d');
    if (!ctx) return;

    const labels = this.data.map((item) => {
      const date = new Date(item.date);
      return `${date.getMonth() + 1}/${date.getDate()}`;
    });

    const values = this.data.map((item) => item.value);

    const config: ChartConfiguration = {
      type: this.type,
      data: {
        labels: labels,
        datasets: [
          {
            label: this.label,
            data: values,
            backgroundColor: this.type === 'bar' ? this.color : `${this.color}33`,
            borderColor: this.color,
            borderWidth: 2,
            fill: this.type === 'line',
            tension: 0.4,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: false,
          },
          tooltip: {
            mode: 'index',
            intersect: false,
          },
        },
        scales: {
          y: {
            beginAtZero: true,
            title: {
              display: !!this.yAxisLabel,
              text: this.yAxisLabel,
            },
          },
          x: {
            ticks: {
              maxRotation: 45,
              minRotation: 45,
            },
          },
        },
      },
    };

    this.chart = new Chart(ctx, config);
  }

  updateChart() {
    if (!this.chart) return;

    const labels = this.data.map((item) => {
      const date = new Date(item.date);
      return `${date.getMonth() + 1}/${date.getDate()}`;
    });

    const values = this.data.map((item) => item.value);

    this.chart.data.labels = labels;
    this.chart.data.datasets[0].data = values;
    this.chart.update();
  }

  ngOnDestroy() {
    if (this.chart) {
      this.chart.destroy();
    }
  }
}
