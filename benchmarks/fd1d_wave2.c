# include <stdlib.h>
# include <stdio.h>

int main ( )
{
  double c = 2.0;
  double dt;
  double dudt;
  double fx;
  double h;
  double h_vec[3] = { 0.1, 0.05, 0.01 };
  int i;
  int j;
  int k;
  int m;
  int m_vec[3] = { 11, 21, 101 };
  double r;
  double r_vec[3] = { 0.5, 0.999, 2.0 };
  double t;
  double *unew;
  double *unow;
  double *uold;
  double x;
/*
  Choose problem parameters.
*/
  for ( i = 0; i < 3; i++ )
  {
    r = r_vec[i];
    for ( j = 0; j < 3; j++ )
    {
      h = h_vec[j];
      m = m_vec[j];
      dt = r * h;
      printf ( "\n" );
      printf ( "  H = %g\n", h );
      printf ( "  M = %d\n", m );
      printf ( "  DT = %g\n", dt );
/*
  Make space.
*/
      uold = ( double * ) malloc ( m * sizeof ( double ) );
      unow = ( double * ) malloc ( m * sizeof ( double ) );
      unew = ( double * ) malloc ( m * sizeof ( double ) );
/*
  Use BC + IC at time 0.
*/
      unew[0] = 0.0;
      for ( k = 1; k < m - 1; k++ )
      {
        x = ( double ) k / ( double ) ( m - 1 );
        unew[k] = x * ( 1.0 - x ) * ( 2.0 - x );
      }
      unew[m-1] = 0.0;
      t = 0.0;
      printf ( "\n" );
      printf ( "T = %g\n", t );
      for ( k = 0; k < m; k++ )
      {
        x = ( double ) k / ( double ) ( m - 1 );
        printf ( "  %10.4g  %10.4g\n", x, unew[k] );
      }
      for ( k = 0; k < m; k++ )
      {
        unow[k] = unew[k];
      }
/*
  Use BC + IC at time dt.
*/
      unew[0] = 0.0;
      for ( k = 1; k < m - 1; k++ )
      {
        dudt = 0.0;
        fx = 0.0;
        unew[k] = unow[k] + dt * dudt + 0.5 * ( dt / c ) * ( dt / c ) 
          * ( fx + ( unow[k-1] - 2.0 * unow[k] + unow[k+1] ) / h / h );
      }
      unew[m-1] = 0.0;
      t = t + dt;
      printf ( "\n" );
      printf ( "T = %g\n", t );
      for ( k = 0; k < m; k++ )
      {
        x = ( double ) k / ( double ) ( m - 1 );
        printf ( "  %10.4g  %10.4g\n", x, unew[k] );
      }
      for ( k = 0; k < m; k++ )
      {
        uold[k] = unow[k];
        unow[k] = unew[k];
      }
/*
  Compute UNEW.
*/
      while ( t < 1.0 )
      {
        unew[0] = 0.0;
        for ( k = 1; k < m - 1; k++ )
        {
          fx = 0.0;
          unew[k] = 2.0 * unow[k] - uold[k]
            + ( dt / c ) * ( dt / c ) * ( fx + ( unow[k-1] - 2.0 * unow[k] + unow[k+1] ) / h / h );
        }
        unew[m-1] = 0.0;
        t = t + dt;
        printf ( "\n" );
        printf ( "T = %g\n", t );
        for ( k = 0; k < m; k++ )
        {
          x = ( double ) k / ( double ) ( m - 1 );
          printf ( "  %10.4g  %10.4g\n", x, unew[k] );
        }
        for ( k = 0; k < m; k++ )
        {
          uold[k] = unow[k];
          unow[k] = unew[k];
        }
      }
/*
  End of computation I, J.
*/
    }
  }

  return 0;
}
