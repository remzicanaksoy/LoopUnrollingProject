# include <stdbool.h>
# include <stdio.h>
# include <stdlib.h>
# include <time.h>

int main ( int argc, char *argv[] );
double *r8mat_uniform_01_new ( int m, int n, int *seed );
void node_write ( char *output_filename, int m, int n, double table[] );
void timestamp ( );

/******************************************************************************/

int main ( int argc, char *argv[] )

/******************************************************************************/
/*
  Purpose:

    MAIN is the main program for RANDOM_NODES.

  Licensing:

    This code is distributed under the GNU LGPL license. 

  Modified:

    21 September 2016

  Author:

    John Burkardt
*/
{
  char filename[80];
  int m;
  int n;
  double *r8mat;
  int seed;
  bool verbose = false;

  if ( verbose )
  {
    timestamp ( );
    printf ( "\n" );
    printf ( "RANDOM_NODES:\n" );
    printf ( "  C version\n" );
    printf ( "  Create a file of N random 2D nodes for triangle().\n" );
  }

  n = atoi ( argv[1] );
  if ( verbose )
  {
    printf ( "  N = %d\n", n );
  }
/*
  Create data.
*/
  m = 2;
  seed = 123456789;
  r8mat = r8mat_uniform_01_new ( m, n, &seed );
/*
  Write data to triangle node file.
*/
  sprintf ( filename, "nodes_n%d.node", n );
  if ( verbose )
  {
    printf ( "  Created '%s'\n", filename );
  }

  node_write ( filename, m, n, r8mat );
/*
  Free memory.
*/
  free ( r8mat );
/*
  Terminate.
*/
  if ( verbose )
  {
    printf ( "\n" );
    printf ( "RANDOM_NODES:\n" );
    printf ( "  Normal end of execution.\n" );
    printf ( "\n" );
    timestamp ( );
  }

  return 0;
}
/******************************************************************************/

void node_write ( char *output_filename, int m, int n, double table[] )

/******************************************************************************/
/*
  Purpose:

    NODE_WRITE writes a triangle node file.

  Discussion:

    An R8MAT is an array of R8's.

  Licensing:

    This code is distributed under the GNU LGPL license. 

  Modified:

    01 June 2009

  Author:

    John Burkardt

  Parameters:

    Input, char *OUTPUT_FILENAME, the output filename.

    Input, int M, the spatial dimension.

    Input, int N, the number of points.

    Input, double TABLE[M*N], the data.
*/
{
  int i;
  int j;
  FILE *output;
/*
  Open the file.
*/
  output = fopen ( output_filename, "wt" );

  if ( !output )
  {
    fprintf ( stderr, "\n" );
    fprintf ( stderr, "R8MAT_WRITE - Fatal error!\n" );
    fprintf ( stderr, "  Could not open the file '%s'.\n", output_filename );
    exit ( 1 );
  }
/*
  TRIANGLE requires a header line.
*/
  fprintf ( output, "%d  2  0  0\n", n );
/*
  Write the data.
*/
  for ( j = 0; j < n; j++ )
  {
    fprintf ( output, "%d  ", i );
    for ( i = 0; i < m; i++ )
    {
      fprintf ( output, "  %24.16g", table[i+j*m] );
    }
    fprintf ( output, "\n" );
  }
/*
  Close the file.
*/
  fclose ( output );

  return;
}
/******************************************************************************/

double *r8mat_uniform_01_new ( int m, int n, int *seed )

/******************************************************************************/
/*
  Purpose:

    R8MAT_UNIFORM_01_NEW fills an R8MAT with pseudorandom values scaled to [0,1].

  Discussion:

    An R8MAT is a doubly dimensioned array of R8 values, stored as a vector
    in column-major order.

    This routine implements the recursion

      seed = 16807 * seed mod ( 2^31 - 1 )
      unif = seed / ( 2^31 - 1 )

    The integer arithmetic never requires more than 32 bits,
    including a sign bit.

  Licensing:

    This code is distributed under the GNU LGPL license.

  Modified:

    30 June 2009

  Author:

    John Burkardt

  Reference:

    Paul Bratley, Bennett Fox, Linus Schrage,
    A Guide to Simulation,
    Springer Verlag, pages 201-202, 1983.

    Bennett Fox,
    Algorithm 647:
    Implementation and Relative Efficiency of Quasirandom
    Sequence Generators,
    ACM Transactions on Mathematical Software,
    Volume 12, Number 4, pages 362-376, 1986.

    Philip Lewis, Allen Goodman, James Miller,
    A Pseudo-Random Number Generator for the System/360,
    IBM Systems Journal,
    Volume 8, pages 136-143, 1969.

  Parameters:

    Input, int M, N, the number of rows and columns.

    Input/output, int *SEED, the "seed" value.  Normally, this
    value should not be 0, otherwise the output value of SEED
    will still be 0, and R8_UNIFORM will be 0.  On output, SEED has
    been updated.

    Output, double R8MAT_UNIFORM_01_NEW[M*N], a matrix of pseudorandom values.
*/
{
  int i;
  const int i4_huge = 2147483647;
  int j;
  int k;
  double *r;

  if ( *seed == 0 )
  {
    fprintf ( stderr, "\n" );
    fprintf ( stderr, "R8MAT_UNIFORM_01_NEW - Fatal error!\n" );
    fprintf ( stderr, "  Input value of SEED = 0\n" );
    exit ( 1 );
  }

  r = ( double * ) malloc ( m * n * sizeof ( double ) );

  for ( j = 0; j < n; j++ )
  {
    for ( i = 0; i < m; i++ )
    {
      k = *seed / 127773;

      *seed = 16807 * ( *seed - k * 127773 ) - k * 2836;

      if ( *seed < 0 )
      {
        *seed = *seed + i4_huge;
      }
      r[i+j*m] = ( double ) ( *seed ) * 4.656612875E-10;
    }
  }

  return r;
}
/******************************************************************************/

void timestamp ( )

/******************************************************************************/
/*
  Purpose:

    TIMESTAMP prints the current YMDHMS date as a time stamp.

  Example:

    31 May 2001 09:45:54 AM

  Licensing:

    This code is distributed under the GNU LGPL license. 

  Modified:

    24 September 2003

  Author:

    John Burkardt

  Parameters:

    None
*/
{
# define TIME_SIZE 40

  static char time_buffer[TIME_SIZE];
  const struct tm *tm;
  size_t len;
  time_t now;

  now = time ( NULL );
  tm = localtime ( &now );

  len = strftime ( time_buffer, TIME_SIZE, "%d %B %Y %I:%M:%S %p", tm );

  printf ( "%s\n", time_buffer );

  return;
# undef TIME_SIZE
}
