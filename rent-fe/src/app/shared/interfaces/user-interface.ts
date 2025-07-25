import { BranchInterface } from "./branch-interface";

export interface UserInterface {
  id: string;
  email: string;
  fullName: string;
  username: string;
  password: string;
  identifier: string;
  enabled: boolean;
  branchId: string;
  roles: string[];
  revisionMetadata: RevisionMetadata;
  inspections: string[];
  directorateCode: string;
  branch: BranchInterface;
}

export interface RevisionMetadata {
  revisionNumber: number;
  revisionInstant: string;
  revisionType: string;
  createdBy: string;
  createdDate: string;
  lastModifiedBy: string;
  lastModifiedDate: string;
}
