from avro import schema, datafile, io
import pprint
import pandas as pd

OUTFILE_NAME = 'countries.avro'
rec_reader = io.DatumReader()
 
df_reader = datafile.DataFileReader(
    open(OUTFILE_NAME),
    rec_reader
)
# Read all records stored inside
pp = pprint.PrettyPrinter()
 
#df= pd.DataFrame(columns=['country_id','name','population','description','area_sqkm'])
l=[]
for record in df_reader:
	l.append(record)
df= pd.DataFrame(l)
df=df[df['population']> 10000000 ]
print len(df.index)
