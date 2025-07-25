export interface Classifier {
  code: string;
  parent: Classifier;
  name: string;
  enabled: boolean;
  description: string;
  descriptionShort: string;
  symbol: string;
  subClassifiers: Classifier[];
  grouped: string;
  filtered: string;
  educationQualificationLevel: Classifier
}
