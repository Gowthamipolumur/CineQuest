// import { Routes } from '@angular/router';
// import { SelectShowtimeComponent } from './select-showtime/select-showtime.component';
// import { SelectSeatComponent } from './select-seat/select-seat.component';
// import { OrderSummaryComponent } from './ordersummary/ordersummary.component';
// import { HomeComponent } from './home/home.component';
// import { MovieDetailsComponent } from './movie-details/movie-details.component';

// export const routes: Routes = [
//   { path: '', component: HomeComponent }, // Home page
//   { path: 'movie/:id', component: MovieDetailsComponent }, // Movie details page
//   { path: 'book/:id', component: SelectShowtimeComponent },
//   { path: 'select-seat', component: SelectSeatComponent },
//   { path: 'order-summary', component: OrderSummaryComponent },
//   { path: '**', redirectTo: '' } // Wildcard route
// ];

import { Routes } from '@angular/router';
import { SelectShowtimeComponent } from './select-showtime/select-showtime.component';
import { SelectSeatComponent } from './select-seat/select-seat.component';
import { OrderSummaryComponent } from './ordersummary/ordersummary.component';
import { HomeComponent } from './home/home.component';
import { MovieDetailsComponent } from './movie-details/movie-details.component';
import { RegistrationComponent } from './registration/registration.component';
import { VerifyAccountComponent } from './verify-account/verify-account.component';
import { EditProfileComponent } from './edit-profile/edit-profile.component';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { LoginComponent } from './login/login.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { AdminPortalComponent } from './admin-portal/admin-portal.component';
import { EditMovieComponent } from './edit-movie/edit-movie.component';
import { ManageMoviesComponent } from './manage-movies/manage-movies.component';
import { ManagePromotionsComponent } from './manage-promotions/manage-promotions.component';
import { ScheduleMovieComponent } from './schedule-movie/schedule-movie.component';
import { AddMovieComponent } from './add-movie/add-movie.component';
import { CheckoutComponent } from './checkout/checkout.component';
import { OrderDetailsComponent } from './order-details/order-details.component';
import {OrderHistoryComponent} from "./order-history/order-history.component";


export const routes: Routes = [
  { path: '', component: HomeComponent },  // Home page
  { path: 'home', component: HomeComponent },  // Home page
  { path: 'movie/:id', component: MovieDetailsComponent },  // Movie details page
  { path: 'book/:id', component: SelectShowtimeComponent },
  { path: 'select-seat', component: SelectSeatComponent },
  { path: 'order-summary', component: OrderSummaryComponent },
  { path: 'checkout', component: CheckoutComponent },
  { path: 'order-details', component: OrderDetailsComponent },
  { path: 'order-history', component: OrderHistoryComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegistrationComponent },  // Registration page
  { path: 'verify', component: VerifyAccountComponent },  // Verification page (should only appear when navigating to '/verify')
  { path: 'edit-profile', component: EditProfileComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'resetPassword', component: ResetPasswordComponent },
  { path: 'resetPassword/:userId', component: ResetPasswordComponent },
  { path: 'admin-portal', component: AdminPortalComponent },
  { path: 'manage-movies', component: ManageMoviesComponent },
  { path: 'manage-promos', component: ManagePromotionsComponent },
  { path: 'add-movie', component: AddMovieComponent },
  { path: 'edit-movie/:id', component: EditMovieComponent },
  { path: 'schedule-movie/:id', component: ScheduleMovieComponent },
  { path: 'change', component: ChangePasswordComponent },  // Verification page (should only appear when navigating to '/verify')
  { path: '**', redirectTo: '' }  // Wildcard route to handle invalid URLs

];

