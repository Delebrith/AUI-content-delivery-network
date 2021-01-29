% number of clients 
int: m;

% number of servers 
int: n;

% number of resources
int: o;

% set of clients 
set of int: I = 1..m;

% set of servers 
set of int: J = 1..n;

% set of resources 
set of int: K = 1..o;

% space consumed in allocating resource k to server i [kB]
array[K] of int: b; 

% requests intensity [rps] of client i for resource k
array[I, K] of int: d; 

% disk capacity of server j 
array[J] of int: s;

% cost of accessing server j by client i [number of redirects]
array[I, J] of int: c; 


% x[i,j,k] = 1 means resource k requested by client i is assigned to server j 
array[I, J, K] of var 0..1: x;

array[J, K] of var 0..1: z;

%
% objective
%
    
var int: obj = sum(i in I, j in J, k in K) (b[k] * d[i, k] * c[i, j] * x[i, j, k]);
solve :: int_search(
        [x[i,j,k] | i in I, j in J, k in K] ++ [z[j,k] | j in J, k in K] ++ [obj],
        first_fail, indomain_min, complete)
     minimize obj;

%
% constraints
%

constraint

  obj >= 0
  /\
  
  % total amount of resources consumed by all jobs assigned to agent i
  %  must not exceed the agent's capacity 
  forall(j in J) ( sum(k in K) (b[k] * z[j,k]) <= s[j])
  /\
  
  forall(i in I, k in K) ( sum (j in J) ( x[i,j,k] ) = 1 )
  /\
  
  forall(i in I, j in J, k in K) ( x[i,j,k] <= z[j,k]  )
  
  ;



output [
  "\nobj: ", show(obj), 
] ++ ["\n"] ++
[
  show(x)
] ++ ["\n"]++
[
  if k = 1 then "\n" else " " endif ++
    show(z[j, k]) 
  | j in J, k in K
] ++ ["\n"];


 % m - number of clients
m = 10;

 % n - number of servers
n = 5;

 % o - number of resources 
o = 10;

 % size of resource K
b = [2, 2, 2, 2, 2, 2, 2, 2, 3, 2];

 % d - request intensity per resource 
d = array2d(I, K, [
  3, 3, 3, 3, 3, 2, 2, 2, 2, 2,
  3, 3, 3, 3, 3, 2, 2, 2, 2, 2,
  3, 3, 3, 3, 3, 2, 2, 2, 2, 2,
  3, 3, 3, 3, 3, 2, 2, 2, 2, 2,
  3, 3, 3, 3, 3, 2, 2, 2, 2, 2,
  3, 3, 3, 3, 3, 2, 2, 2, 2, 2,
  3, 3, 3, 3, 3, 2, 2, 2, 2, 2,
  3, 3, 3, 3, 3, 2, 2, 2, 2, 2,
  3, 3, 3, 3, 3, 2, 2, 2, 2, 2,
  3, 3, 3, 3, 3, 2, 2, 2, 2, 2]
);

 % s - disk capacity 
s = [5, 3, 5, 7, 10];

 % c - costs of accessing server J by client I
c = array2d(I, J, [
  0, 1, 1, 2, 2, 
  0, 1, 1, 2, 2, 
  2, 0, 1, 1, 2, 
  2, 0, 1, 1, 2, 
  2, 0, 1, 1, 2, 
  2, 2, 0, 1, 1, 
  1, 2, 2, 0, 1, 
  1, 2, 2, 0, 1, 
  1, 1, 2, 2, 0, 
  1, 1, 2, 2, 0]
);
