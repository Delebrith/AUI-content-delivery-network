import os
import sys
import requests

def duplicates(lst, item):
    return [i for i, x in enumerate(lst) if x == item]

def main():
    filename = sys.argv[1]
    print(filename)
    
    with open(filename) as f:
        mapping = {}
        lines = f.readlines()
        count = 0
        for l in lines:
            mapping[count] = l
            count += 1
            
        print(mapping.keys())
        for i, line in mapping.items():
            base_address = sys.argv[2]
            for j in duplicates(line.split(), '1'):
                with open('./resources/' + str(j) + '.png', 'rb') as resource:
                    address = base_address + ':808' + str(i) + "/resource/" + str(j)
                    print(address)
                    
                    response = requests.put(
                        address, 
                        files={'content': resource.read(), 'Content-Type': 'image/png'}
                    )
                    
                    print(response)


if __name__ == '__main__':
    main()
    
    
    