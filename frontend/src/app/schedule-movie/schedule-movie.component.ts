import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { MoviesService } from '../services/movies/movies.service';

@Component({
  selector: 'app-schedule-movie',
  standalone: true,
  imports: [CommonModule, FormsModule,RouterModule],
  templateUrl: './schedule-movie.component.html',
  styleUrl: './schedule-movie.component.scss'
})
export class ScheduleMovieComponent {
  theaters: any[] = [];
  movieId: any = null;
  theater: any = null;
  date: string = '';
  time: string = '';
  token: any = null;

  constructor(private http: HttpClient, private route: ActivatedRoute, private moviesService: MoviesService) { }

  ngOnInit(){
    this.route.params.subscribe(params => {
      this.movieId = +params['id'];
    });
    this.loadTheaters();
    this.token = localStorage.getItem('authToken');
  }

  loadTheaters() {
    this.http.get<any>('http://localhost:8080/api/theaters/all').subscribe({
      next: (response) => {
        this.theaters = response.theaters;
      },
      error: (err) => {
        console.error('Failed to load theaters:', err);
      }
    });
  }

  onSubmit() {
    if (this.movieId === null || this.theater === null || this.date === '' || this.time === '') {
      alert("Please enter all fields!");
    } else {
      const theaterId: number = +this.theater;
      this.moviesService.scheduleMovie(this.movieId, theaterId, this.date, this.time, this.token).subscribe({
        next: (response) => {
          alert("Show scheduled successfully!")
          console.log('Response:', response)
        },
        error: (error) => {
          alert("Error: " + error.error.message)
          console.error('Error:', error)
        }
      });
    }
  }

}
