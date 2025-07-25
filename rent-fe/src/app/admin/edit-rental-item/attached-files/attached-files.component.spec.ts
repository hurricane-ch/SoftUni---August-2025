import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AttachedFilesComponent } from './attached-files.component';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { RouterModule } from '@angular/router';

describe('AttachedFilesComponent', () => {
  let component: AttachedFilesComponent;
  let fixture: ComponentFixture<AttachedFilesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AttachedFilesComponent],
      imports: [RouterModule.forRoot([])],
      providers: [provideHttpClient(withInterceptorsFromDi())]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AttachedFilesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
